package com.example.gboard.data

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

object Network {
	private var rHandlerField: Handler? = null
	private val rHandler: Handler
		get() {
			if (rHandlerField == null) {
				if (rHandlerThread == null) {
					rHandlerThread = HandlerThread("DataReceiver")
					rHandlerThread!!.start()
				}
				rHandlerField = Handler(rHandlerThread!!.looper)
			}
			return rHandlerField!!
		}
	private var rHandlerThread: HandlerThread? = null

	private var tHandlerField: Handler? = null
	private val tHandler: Handler
		get() {
			if (tHandlerField == null) {
				if (tHandlerThread == null) {
					tHandlerThread = HandlerThread("DataTransmitter")
					tHandlerThread!!.start()
				}
				tHandlerField = Handler(tHandlerThread!!.looper)
			}
			return tHandlerField!!
		}
	private var tHandlerThread: HandlerThread? = null


	val receivePacket = OnChanger<DatagramPacket>(DatagramPacket(ByteArray(1), 1))
	val ping = OnChanger<Long>(0L)
	val startGameFlag = OnChanger<Byte>(0)

	const val CONNECTION_ESTABLISHED: Byte = 124
	const val CONNECTION_STARTED: Byte = 125
	const val CONNECTION_REFUSED: Byte = 126
	const val CONNECTION_END: Byte = 127

	const val GAME_STARTED: Byte = 123
	const val SNAKE_DIRECTION: Byte = 122
	const val SNAKE_VELOCITY: Byte = 121
	const val SNAKE_COLLIDED: Byte = 120
	const val SNAKE_RESPAWN: Byte = 119
	const val SNAKE_FINISHED: Byte = 118
	const val RETRY_FLAG: Byte = 117

	private var socket: DatagramSocket? = null
	private var address = InetAddress.getByName(/*"192.168.1.5"*/"95.142.45.201")
	private var port = 4444
	private var sendPacketTime = 0L

	fun sendDirection(cos: Float, sin: Float) {
		val cosBytes = ByteBuffer.allocate(4).putFloat(cos).array()
		val sinBytes = ByteBuffer.allocate(4).putFloat(sin).array()
		val flag = byteArrayOf(SNAKE_DIRECTION)
		val msg = flag + cosBytes + sinBytes
		tHandler.post {
			socket?.send(DatagramPacket(msg, 9, address, port))
		}
	}

	fun sendVelocity(value: Float) {
		val velocity = ByteBuffer.allocate(4).putFloat(value).array()
		val flag = byteArrayOf(SNAKE_VELOCITY)
		val msg = flag + velocity
		tHandler.post {
			socket?.send(DatagramPacket(msg, 5, address, port))
		}
	}

	fun sendRequestToServer(level: Byte) {
		if (socket == null) {
			socket = DatagramSocket()
		}
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(CONNECTION_STARTED, level), 2, address, port))
			sendPacketTime = System.currentTimeMillis()
		}
	}

	fun sendStartFlag() {
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(GAME_STARTED), 1, address, port))
		}
	}

	fun sendCollidedFlag() {
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(SNAKE_COLLIDED), 1, address, port))
		}
	}

	fun sendRetryFlag() {
		Log.e("Send", "retry")
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(RETRY_FLAG), 1, address, port))
		}
	}

	fun sendRespawnFlag() {
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(SNAKE_RESPAWN), 1, address, port))
		}
	}

	fun sendFinishFlag() {
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(SNAKE_FINISHED), 1, address, port))
		}
	}

	fun waitGameStartFlag() {
		rHandler.post {
			val packet = DatagramPacket(ByteArray(1), 1)
			try {
				socket?.receive(packet)
			} catch (e: IOException) {
			}
			val bytes = packet.data
			if (bytes[0] == GAME_STARTED)
				startGameFlag.value = bytes[0]
		}
	}

	fun receiveMessage() {
		rHandler.post {
			val packet = DatagramPacket(ByteArray(100), 100)
			try {
				socket?.receive(packet)
			} catch (e: IOException) {
			}
			receivePacket.value = packet
			ping.value = System.currentTimeMillis() - sendPacketTime
		}
	}

	fun disconnect() {
		tHandler.post {
			socket?.send(DatagramPacket(byteArrayOf(CONNECTION_END), 1, address, port))
			socket?.close()
			socket = null
		}
		rHandler.removeCallbacksAndMessages(null)
		receivePacket.onChange.clear()
		ping.onChange.clear()
		receivePacket.value = DatagramPacket(ByteArray(1), 1)
		startGameFlag.value = 0
	}
}