package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

//class SleepNightAdapter: RecyclerView.Adapter<TextItemViewHolder>() {
//class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() {
//class SleepNightAdapter: ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
//class SleepNightAdapter(val clickListener : SleepNightListener): ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
class SleepNightAdapter(val clickListener : SleepNightListener): ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

//    // ListAdapter가 사용되면 이게 필요 없음!
//    var data = listOf<SleepNight>()
//        set(value) {
//            field = value
//            notifyDataSetChanged() // 이거 대신 DiffUtil을 사용하세요!
//        }
//    //이것도 ListAdapter로 처리 가능
//    override fun getItemCount() = data.size

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    // refactor
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
//        return ViewHolder(view)

//        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false)

    // move to companion object : 여기 맨 밑에 companion object로 옮겨감!
//        return from(parent)

//        return ViewHolder.from(parent) // private constructor이기 때문에 from()을 함부로 호출할 수 없게 만듬!
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw java.lang.ClassCastException("Unknown viewType ${viewType}")
        }
    }

    // move to companion object
//    fun from(parent: ViewGroup): ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
//        return ViewHolder(view)
//    }

    //    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
//        val item = data[position]
//
//        if (item.sleepQuality <= 1) {
//           holder.textView.setTextColor(Color.RED)
//        } else {
//            holder.textView.setTextColor(Color.BLACK)
//        }
//
//        holder.textView.text = item.sleepQuality.toString()
//    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        // ListAdapter로 처리하기
//        val item = data[position]
//        val item = getItem(position)

        // refactor가 끝나면 res는 ViewHolder 내에서 얻을 수 있으므로!
//        val res = holder.itemView.context.resources
//        bind(holder, item, res) // convert parameter to receiver
//        holder.bind(item, res)

//        holder.bind(item)

//        holder.bind(clickListener, getItem(position)!!)
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(clickListener, nightItem.sleepNight)
            }
        }
    }

    // holder에서 convert parameter to receiver로 변경해야...
//    private fun bind(
//        holder: ViewHolder,
//        item: SleepNight,
//        res: Resources
//    ) {
//        holder.sleepLength.text =
//            convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
//        holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
//        holder.qualityImage.setImageResource(
//            when (item.sleepQuality) {
//                0 -> R.drawable.ic_sleep_0
//                1 -> R.drawable.ic_sleep_1
//                2 -> R.drawable.ic_sleep_2
//                3 -> R.drawable.ic_sleep_3
//                4 -> R.drawable.ic_sleep_4
//                5 -> R.drawable.ic_sleep_5
//                else -> R.drawable.ic_sleep_active
//            }
//        )
//    }

    // 그래서 fun ViewHolder.bind( 내용을 복사해서
//    private fun ViewHolder.bind(
//        item: SleepNight,
//        res: Resources
//    ) {
//        sleepLength.text =
//            convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
//        quality.text = convertNumericQualityToString(item.sleepQuality, res)
//        qualityImage.setImageResource(
//            when (item.sleepQuality) {
//                0 -> R.drawable.ic_sleep_0
//                1 -> R.drawable.ic_sleep_1
//                2 -> R.drawable.ic_sleep_2
//                3 -> R.drawable.ic_sleep_3
//                4 -> R.drawable.ic_sleep_4
//                5 -> R.drawable.ic_sleep_5
//                else -> R.drawable.ic_sleep_active
//            }
//        )
//    }

//    class ViewHolder private constructor(itemView: ListItemSleepNightBinding): RecyclerView.ViewHolder(itemView) {
    // binding.root는 Constraint layout
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding): RecyclerView.ViewHolder(binding.root) {
    //        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
    //        val quality: TextView = itemView.findViewById(R.id.quality_string)
    //        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

            // inline property로 refactor
            // val sleepLength: TextView = binding.sleepLength
            // val quality: TextView = binding.qualityString
            // val qualityImage: ImageView = binding.qualityImage

        // 그래서 fun ViewHolder.bind( 내용을 여기에 붙여 넣기
//        fun bind(item: SleepNight) {
//            val res = itemView.context.resources
//
//            // inline property로 refactor
//    //        sleepLength.text =
//    //            convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
//    //        quality.text = convertNumericQualityToString(item.sleepQuality, res)
//    //        qualityImage.setImageResource(
//    //            when (item.sleepQuality) {
//    //                0 -> R.drawable.ic_sleep_0
//    //                1 -> R.drawable.ic_sleep_1
//    //                2 -> R.drawable.ic_sleep_2
//    //                3 -> R.drawable.ic_sleep_3
//    //                4 -> R.drawable.ic_sleep_4
//    //                5 -> R.drawable.ic_sleep_5
//    //                else -> R.drawable.ic_sleep_active
//    //            }
//    //        )
//
//            binding.sleepLength.text =
//                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
//
//            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
//            binding.qualityImage.setImageResource(
//                when (item.sleepQuality) {
//                    0 -> R.drawable.ic_sleep_0
//                    1 -> R.drawable.ic_sleep_1
//                    2 -> R.drawable.ic_sleep_2
//                    3 -> R.drawable.ic_sleep_3
//                    4 -> R.drawable.ic_sleep_4
//                    5 -> R.drawable.ic_sleep_5
//                    else -> R.drawable.ic_sleep_active
//                }
//            )
//        }
        // 다 필요 없음....BindingUtils 이용
        fun bind(clickListener: SleepNightListener, item: SleepNight) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                // list_item_sleep_night에 data binding 하면서 이렇게 inflate하지 않아도 돼
//                val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
//                return ViewHolder(view)

                // ListItemSleepNightBinding은 xml에 data binding하면 자동 생성성
               val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    // move to companion object -> ViewHolder class로 옮김
//    companion object {
//        fun from(parent: ViewGroup): ViewHolder {
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
//            return ViewHolder(view)
//        }
//    }

}

//class SleepNightDiffCallback: DiffUtil.ItemCallback<SleepNight>(){
//    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
//        return oldItem.nightId == newItem.nightId
//    }
//
//    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
//        return oldItem == newItem
//    }
//}

class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem {
    data class SleepNightItem(val sleepNight: SleepNight): DataItem(){
        override val id = sleepNight.nightId
    }

    object Header: DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header, parent, false)
            return TextViewHolder(view)
        }
    }
}