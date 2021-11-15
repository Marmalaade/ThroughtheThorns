package com.example.gboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.slide_item_container.view.*

class SliderAdapter internal constructor(
	sliderItems: MutableList<SliderItem>,
	viewPager: ViewPager2,
	private val pageClickListener: OnPageClickListener
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {


	private val sliderItems: List<SliderItem>

	init {
		this.sliderItems = sliderItems
	}


	class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val imageView: RoundedImageView = itemView.imageSlide
		fun image(sliderItem: SliderItem) {
			imageView.setImageResource(sliderItem.image)
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
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
		holder.itemView.setOnClickListener {
			pageClickListener.onPageClick(position,sliderItems[position])
		}
	}

	override fun getItemCount(): Int {
		return sliderItems.size
	}

}