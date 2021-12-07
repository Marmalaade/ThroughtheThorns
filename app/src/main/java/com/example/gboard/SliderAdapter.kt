package com.example.gboard

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gboard.data.Settings
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.slide_item_container.view.*

class SliderAdapter internal constructor(
	sliderItems: MutableList<SliderItem>,
	viewPager: ViewPager2,
	private val pageClickListener: OnPageClickListener
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {


	private val sliderItems: List<SliderItem>

	private var playImage: Drawable? = null
	private var lockImage: Drawable? = null

	init {
		this.sliderItems = sliderItems
	}


	class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val imageView: RoundedImageView = itemView.imageSlide
		val playButton: ImageButton = itemView.playButton
		fun image(sliderItem: SliderItem) {
			imageView.setImageResource(sliderItem.image)
		}

		fun onClick(onClickListener: View.OnClickListener) {
			imageView.setOnClickListener(onClickListener)
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
		playImage = ContextCompat.getDrawable(parent.context, R.drawable.ic_play_button)
		lockImage = ContextCompat.getDrawable(parent.context, R.drawable.ic_lock)
		return SliderViewHolder(
			LayoutInflater.from(parent.context).inflate(
				R.layout.slide_item_container,
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
		holder.image(sliderItems[position])
		holder.playButton.background = if (Settings.levelPurchases[position]) playImage else lockImage
		holder.onClick {
			pageClickListener.onPageClick(position, sliderItems[position])
		}
	}

	override fun getItemCount(): Int {
		return sliderItems.size
	}

}