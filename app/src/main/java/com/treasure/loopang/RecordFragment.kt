package com.treasure.loopang

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.audiov2.Mixer
import com.treasure.loopang.audiov2.Recorder
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.ui.adapter.LayerListAdapter
import com.treasure.loopang.ui.listener.TouchGestureListener
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.dialog_save_loop.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlin.math.abs

class RecordFragment : androidx.fragment.app.Fragment() {
    private val mLayerListAdapter : LayerListAdapter = LayerListAdapter()
    private val mTouchGestureListener = TouchGestureListener()

    private var mMixer: Mixer = Mixer()
    private var mRecorder: Recorder = Recorder()

    private val mFileManager = FileManager()
    private val mDirectoryPath = mFileManager.looperDir.absolutePath

    private var mLoopPlaybackState: Boolean = false
    private var mRecordState: Boolean = false

    init{
        mTouchGestureListener.onSingleTap = { onThisSingleTap() }
        mTouchGestureListener.onSwipeToDown = { onThisSwipeToDown() }
        mTouchGestureListener.onSwipeToUp = { onThisSwipeToUp() }

        initRecorder()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layer_list.adapter = mLayerListAdapter

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, mTouchGestureListener)
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}

        /* 리스트 아이템 롱클릭 이벤트 설정 */
        layer_list.isLongClickable = true
        layer_list.setOnItemLongClickListener{ parent, v, position, id ->
            onLayerListItemLongClick(parent, v, position, id)
        }

        /* 리스트 아이템 싱글 클릭 이벤트 설정 */
        layer_list.setOnItemClickListener { parent, v, position, id ->
            onLayerListItemClick(parent, v, position, id)
        }
    }

    override fun onDestroy() {
        mRecorder.stop()
        mMixer.stop()
        super.onDestroy()
        Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RecordFragment", "RecordFragment Paused!")
    }

    private fun initRecorder(){
        mRecorder.onSuccess {
            addLayer()
            mRecordState = false
        }
        mRecorder.onStart {
            mRecordState = true
        }
        // mRecorder.onSuccess { Log.d("mRecorder","mRecorder.onSuccess()")}
        // For Realtime Visualizer
        mRecorder.addEffector {
            realtime_visualizer_view.analyze(
                it.fold(0) { acc, next->
                    acc + abs(next.toInt())
                } / it.size
            )
            it
        }
    }

    private fun initMixer() {}

    private fun addLayer() {
        activity?.runOnUiThread {
            toast(R.string.toast_record_stop)
            if(realtime_visualizer_view.visibility == View.VISIBLE) {
                realtime_visualizer_view.clear()
                realtime_visualizer_view.visibility = View.GONE
            }
        }

        val sound = mRecorder.getSound()
        mMixer.addSound(sound)
        if(!mMixer.isLooping.get()) mMixer.start()
        mLayerListAdapter.addLayer(sound)
    }

    private fun onThisSingleTap(): Boolean {
        if(mMixer.sounds.isNotEmpty() && !mMixer.isLooping.get()){
            toast(R.string.toast_record_start_error_without_playback)
        }
        else if (mRecordState) {
            mRecorder.stop()
        }
        else {
            toast(R.string.toast_record_start)
            if(realtime_visualizer_view.visibility == View.GONE) {
                val animation: Animation = AlphaAnimation(0F, 1F)
                animation.duration = 1000
                realtime_visualizer_view.visibility = View.VISIBLE
                realtime_visualizer_view.animation = animation
            }
            if(mMixer.sounds.isNotEmpty()) { mRecorder.start(mMixer.sounds[0].data.size) }
            else { mRecorder.start() }
        }

        return true
    }

    private fun onThisSwipeToUp() {
        if (mRecordState){
            toast(R.string.toast_playback_stop_error)
            return
        }
        if (!mMixer.isLooping.get() && mMixer.sounds.isEmpty()){
            toast(R.string.toast_record_start)
        }
        else if (mMixer.isLooping.get()) {
            toast(R.string.toast_playback_stop)
            mMixer.stop()
        }
        else {
            toast(R.string.toast_playback_start)
            mMixer.start()
        }
    }

    private fun onThisSwipeToDown() {
        Log.d("RecordFragmentTest", "아래로 스와이프 하셨습니다.")
        if(mRecordState) {
            toast(R.string.toast_save_error_while_record)
            return
        }
        if(mMixer.isLooping.get()) mMixer.stop()

        showSaveLoopDialog()
    }

    /* 리스트 아이템 클릭 시 처리동작 (onItemClick 함수와 같이 사용) */
    private fun onLayerListItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("RecordFragmentTest", "아이템 클릭! postion: $position")
    }

    private fun onLayerListItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long) : Boolean {
/*        if(mMixer.isPlaying.get()) return false
        val menuList = listOf("Drop Track")
        val context = this.context!!

        MaterialDialog(context).show {
            listItems(items = menuList) { _, index, _ ->
                when (index) {
                    0 -> {
                        looper.mMixer.sounds.removeAt(looper.mMixer.sounds.size - (position+1))
                        trackListAdapter.removeItem(position)
                        Toast.makeText(this.context, "track is droped!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        Log.d("RecordFragmentTest", "아이템 롱 클릭! postion: $position")
        return true*/
        return false
    }

    private fun showSaveLoopDialog() {
        if(mMixer.sounds.isEmpty()){
            toast(R.string.toast_save_error_no_layer)
            return
        }

        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            noAutoDismiss()
            title(R.string.title_save_loop)
            customView(R.layout.dialog_save_loop, horizontalPadding = true)
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_save) {
                // callback on positive button click
                val loopTitle = it.edit_loop_title.text.toString()
                if(loopTitle == ""){
                    toast(R.string.toast_save_error_without_title)
                } else {
                    val fileType = spinner.selectedItem.toString()
                    val isSaveChecked = check_split.isChecked
                    Log.d("SaveDialog", "\nloopTitle: $loopTitle, \nfileType: $fileType, \nisSaveChecked: $isSaveChecked")
                    val isSaved = saveLoop(mMixer, loopTitle, fileType, isSaveChecked)

                    if(isSaved) toast(getString(R.string.toast_save))
                    else toast(R.string.toast_save_error_failed)

                    it.dismiss()
                }
            }
            negativeButton(R.string.btn_cancel) {
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    private fun saveLoop(mixer: Mixer, loopTitle: String, fileType: String, isSplitChecked: Boolean = false): Boolean {
        if(mixer.sounds.isEmpty()){
            toast(R.string.toast_save_error_no_layer)
            return false
        }

        if (isSplitChecked) {
            // 나눠서 저장할 경우
            val fileLabelList = (1..mixer.sounds.size).map {
                "/${loopTitle}_$it.${fileType.toLowerCase()}"
            }
            mixer.sounds.forEachIndexed { index, sound ->
                sound.save(mDirectoryPath+fileLabelList[index])
            }
        } else {
            // 나누지 않을 경우
            val fileLabel = "/$loopTitle.${fileType.toLowerCase()}"
            Sound(mixer.mixSounds()).save(mDirectoryPath+fileLabel)
        }

        return true
    }

    private fun dropLayer(position: Int) {

    }
}
