//package com.audionowdigital.android.openplayerdemo
//
////import com.olli.ui.util.playogg.Player
////import com.olli.ui.util.playogg.PlayerEvents
//import android.annotation.SuppressLint
//import android.media.MediaPlayer
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Message
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.audionowdigital.android.openplayer.Player
//import com.audionowdigital.android.openplayer.PlayerEvents
//import com.github.piasy.rxandroidaudio.StreamAudioPlayer
//import com.olli.domain.eventbus.KBus
//import com.olli.domain.extension.valueOrEmpty
//import com.olli.domain.model.eventbus.SendMessageEventBus
//import com.olli.domain.model.eventbus.ShowKeyBoardSendMessageEventBus
//import com.olli.domain.model.network.request.message.Event
//import com.olli.domain.model.network.request.message.Header
//import com.olli.domain.model.network.request.message.MetaStreamRequest
//import com.olli.domain.model.network.request.message.PayloadDevice
//import com.olli.domain.util.ConstAPI
//import com.olli.domain.util.ConstApp
//import com.olli.domain.util.Unique
//import com.olli.presentation.features.message.ChatContract
//import com.olli.presentation.features.message.MessageResourceProvider
//import com.olli.presentation.features.message.model.MessageViewModel
//import com.olli.ui.R
//import com.olli.ui.base.controller.viewcontroller.ViewController
//import com.olli.ui.base.listview.view.RecyclerViewController
//import com.olli.ui.base.listview.view.factory.LinearRenderConfigFactory
//import com.olli.ui.extension.view.gone
//import com.olli.ui.extension.view.visible
//import com.olli.ui.features.chat.mapper.MessageViewHolderModelMapper
//import com.olli.ui.features.chat.renderer.*
//import com.olli.ui.features.chat.viewholder.BottomMessageViewHolderModel
//import com.olli.ui.features.chat.viewholder.TextMessageViewHolderModel
//import com.olli.ui.features.home.HomeViewController
//import com.olli.ui.util.handel.DoubleTouchPrevent
//import com.olli.ui.util.system.LogDebug
//import com.olli.ui.util.view.AppNotify
//import io.reactivex.Observable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.item_error_get_data_message.view.*
//import kotlinx.android.synthetic.main.screen_tab_chats.view.*
//import okio.*
//import org.koin.core.inject
//import java.io.*
//import java.io.IOException
//
//class ChatViewController() : ViewController(null), ChatContract.View,
//    MediaPlayer.OnPreparedListener {
//    private val presenter by inject<ChatContract.Presenter>()
//    private val appNotify by inject<AppNotify>()
//    private val provider by inject<MessageResourceProvider>()
//    private val doubleTouchPrevent by inject<DoubleTouchPrevent>()
//    private lateinit var rvController: RecyclerViewController
//
//    constructor(targetController: ViewController) : this() {
//        setTargetController(targetController)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup,
//        savedViewState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.screen_tab_chats, container, false)
//    }
//
//    override fun initPostCreateView(view: View) {
//        presenter.attachView(this)
//        initView(view)
//        initEventBus()
//        handleView(view)
//        presenter.getListMessage(isLoadMore = false)
//        presenter.connectChatBot(isFirstConnect = true)
//    }
//
//    private fun initEventBus() {
//        KBus.subscribe<SendMessageEventBus>(this) {
//            sendMessage(it.contentMessage)
//        }
//        KBus.subscribe<ShowKeyBoardSendMessageEventBus>(this) {
//            rvController.scrollToBottom()
//        }
//    }
//
//    override fun showLogResponse(data: String) {
//        LogDebug.d("receive sendMessage: $data")
//    }
//
//    private var mediaPlayer: MediaPlayer? = MediaPlayer()
//
//    private val lstAudio = mutableListOf<String>()
//    private var isPlayedAudio = false
//
//    var convertedFile: File? = null
//    var fileOutputStream: FileOutputStream? = null
//
//    var lstBuffer = mutableListOf<ByteArray>()
//    var isPlayStreamAudio = false
//    var byteArrayOutputStream: ByteArrayOutputStream? = null
//    private var threadPlayAudio: Thread? = null
//
//    override fun playStream(inputStream: InputStream) {
//        threadPlayAudio = Thread {
//            player.setDataSource(
//                inputStream,
//                215
//            )
//        }
//        threadPlayAudio?.start()
//    }
//
//    //    var read = 0
//    override fun playStreamOgg(buffer: Buffer) {
//        if (buffer.size > 0) {
//            val inputStream = buffer.inputStream()
//            if (byteArrayOutputStream == null) {
//                byteArrayOutputStream = ByteArrayOutputStream()
//            }
//            var read = 0
//            while (read != -1) {
//                read = inputStream.read()
//                byteArrayOutputStream?.write(read)
//            }
//            inputStream.close()
//            byteArrayOutputStream?.let { byteArrayOutputStream ->
//                LogDebug.d("Stream Ogg size -------- : ${byteArrayOutputStream.size()}")
//                if (byteArrayOutputStream.size() > 8192) {
//                    byteArrayOutputStream.flush()
//                    LogDebug.d("Stream Ogg size: ${byteArrayOutputStream.size()}")
//                    lstBuffer.add(byteArrayOutputStream.toByteArray())
//                    byteArrayOutputStream.close()
//                    this.byteArrayOutputStream = null
//                }
//
//            }
//
////            if(!isPlayStreamAudio){
////                isPlayStreamAudio = true
////                playStreamAudio()
////            }
//        }
//
//    }
//
//    private fun playStreamAudio() {
//        try {
//            mediaPlayer?.let { mediaPlayer ->
//                if (lstBuffer.isNotEmpty()) {
//                    val buffer = lstBuffer.first()
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        mediaPlayer.setDataSource(ByteArrayMediaDataSource(buffer))
//                    }
//                    mediaPlayer.prepareAsync()
//                    mediaPlayer.setOnPreparedListener {
//                        mediaPlayer.start()
//                    }
//                    mediaPlayer.setOnCompletionListener {
//                        mediaPlayer.reset()
//                        lstBuffer.removeAt(0)
//                        if (lstBuffer.isEmpty()) {
//                            isPlayStreamAudio = false
//                        } else {
//                            playListInputStream()
//                        }
//                    }
//                }
//            }
//        } catch (e: IllegalArgumentException) {
//            LogDebug.d("buffer: IllegalArgumentException" + e.message.valueOrEmpty())
//        } catch (e: IllegalStateException) {
//            LogDebug.d("buffer: IllegalStateException" + e.message.valueOrEmpty())
//        } catch (e: IOException) {
//            LogDebug.d("buffer: IOException" + e.message.valueOrEmpty())
//        }
//
//
//    }
//
//    override fun saveFileOgg(buffer: Buffer) {
//        try {
//            activity?.let { activity ->
//                if (convertedFile == null) {
//                    convertedFile = File.createTempFile(
//                        "full_omni_text_to_speech_${System.currentTimeMillis()}_${Unique.RequestCode.next()}",
//                        ".ogg",
//                        activity.filesDir
//                    )
//                    fileOutputStream = FileOutputStream(convertedFile)
//                }
//                if (buffer.size > 0) {
////                    LogDebug.d("Create File from buffer")
//                    val bufferOut = ByteArray(1024)
//                    var length: Int
//                    while (buffer.inputStream().read(bufferOut).also { length = it } !== -1) {
//                        fileOutputStream?.write(bufferOut, 0, length)
//                    }
//
//////                fileOutputStream.flush()
////                    fileOutputStream.close()
//
//
////                    val fileOGG = File.createTempFile("omni_text_to_speech_${System.currentTimeMillis()}_${Unique.RequestCode.next()}", ".ogg", activity.filesDir)
//////                    if (!fileOGG.exists()) {
//////                        val fileOutputStream = FileOutputStream(fileOGG)
//////                        fileOutputStream.write(buffer.readByteArray())
//////                        fileOutputStream.close()
//////                    }
////                    val source = buffer.inputStream().source().buffer()
////                    val sink = fileOGG.sink().buffer()
////                    source.use { input ->
////                        sink.use { output ->
////                            output.writeAll(input)
////                        }
////                    }
//                }
//            }
//
//        } catch (e: Exception) {
//            LogDebug.e("", e)
//        }
//    }
//
//    override fun readOggFiledDone() {
//        LogDebug.d("Create File from buffer")
//        convertedFile?.let { convertedFile ->
//            lstAudio.add(convertedFile.path)
//        }
//        fileOutputStream?.close()
//        convertedFile = null
//        fileOutputStream = null
//        playListInputStream()
//
////        byteArrayOutputStream?.let { byteArrayOutputStream ->
////            byteArrayOutputStream.flush()
////            LogDebug.d("Stream Ogg size final: ${byteArrayOutputStream.size()}")
////            lstBuffer.add(byteArrayOutputStream.toByteArray())
////            byteArrayOutputStream.close()
////            this.byteArrayOutputStream = null
////        }
////
////            testStreamOgg()
//
//
//    }
//
//    private fun testStreamOgg() {
//        LogDebug.d("Stream Ogg size: play first")
//        try {
//                if (lstBuffer.isNotEmpty()) {
//                    if (mediaPlayer == null) {
//                        mediaPlayer = MediaPlayer()
//                    }
//                    val buffer = lstBuffer.first()
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        mediaPlayer?.setDataSource(ByteArrayMediaDataSource(buffer))
//                    }
//                    mediaPlayer?.prepareAsync()
//                    mediaPlayer?.setOnPreparedListener {
//                        it.start()
//                    }
//                    mediaPlayer?.setOnCompletionListener {
//                        it.reset()
//                        this.mediaPlayer = null
////                    mediaPlayer.stop()
////                    lstBuffer.removeAt(0)
////                    if (lstBuffer.isEmpty()) {
////                        isPlayStreamAudio = false
////                    } else {
////                        playListInputStream()
////                    }
//                    }
//                }
//        } catch (e: IllegalArgumentException) {
//            LogDebug.d("buffer: IllegalArgumentException" + e.message.valueOrEmpty())
//        } catch (e: IllegalStateException) {
//            LogDebug.d("buffer: IllegalStateException" + e.message.valueOrEmpty())
//        } catch (e: IOException) {
//            LogDebug.d("buffer: IOException" + e.message.valueOrEmpty())
//        }
//    }
//
//    override fun playAudio(buffer: Buffer) {
////        buffer.readByteArray()
////        Buffer()
////
////
////        val bufferResult = Buffer()
////        bufferResult.write(buffer)
////        val path = buffer.inputStream()
////        testplayOggFile(path)
////        path.close()
////        buffer.readUtf8Line()
//
////        LogDebug.d("buffer playAudio")
////        val readUtf8Line = buffer.readUtf8Line()
////        val inputStream = buffer.inputStream()
//        if (buffer.size > 0) {
////            LogDebug.d("buffer: $buffer")
////        val byteArrayInputStream = ByteArrayInputStream(buffer.toByteArray())
//            try {
//                activity?.let { activity ->
//                    val convertedFile = File.createTempFile(
//                        "omni_text_to_speech_${System.currentTimeMillis()}_${Unique.RequestCode.next()}",
//                        ".ogg",
//                        activity.filesDir
//                    )
//                    val fileOutputStream = FileOutputStream(convertedFile)
//                    val bufferOut = ByteArray(1024)
//                    var length: Int
//                    while (buffer.inputStream().read(bufferOut).also { length = it } !== -1) {
//                        fileOutputStream.write(bufferOut, 0, length)
//                    }
//////                fileOutputStream.flush()
//                    fileOutputStream.close()
//
////                    playFileOutputStream(convertedFile)
////                    val inputStream: InputStream = FileInputStream("input")
////                    IOUtils.copy(inputStream, fileOutputStream, true)
//                    try {
//
////                        val mBuffer = ByteArray(2048)
////                        Observable.just<File>(convertedFile)
////                            .subscribeOn(Schedulers.io())
////                            .subscribe(
////                                { file: File? ->
////                                    try {
//////                                        mStreamAudioPlayer.init()
////                                        val inputStream = FileInputStream(file)
////                                        var read: Int
////                                        while (inputStream.read(mBuffer).also { read = it } > 0) {
////                                            mediaPlayer.play(mBuffer, read)
////                                        }
////                                        inputStream.close()
////                                        mStreamAudioPlayer.release()
////                                    } catch (e: IOException) {
////                                        e.printStackTrace()
////                                    }
////                                }
////                            ) { obj: Throwable -> obj.printStackTrace() }
//
////
//                        val path = convertedFile.absolutePath
//                        lstAudio.add(path)
//
////                        val fileInputStream = FileInputStream(convertedFile)
////                        lstAudio.add(fileInputStream)
//                        if (!isPlayedAudio) {
//                            isPlayedAudio = true
//                            playListInputStream()
//                        }
//////                        testplayOggFile(path)
//////                        mediaPlayer.release()
////                        mediaPlayer.reset()
//
//
////                        if (!mediaPlayer.isPlaying) {
////                            mediaPlayer.setDataSource(path)
//////                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
////                            mediaPlayer.prepare()
//////                            mediaPlayer.setOnPreparedListener(this)
//////                            mediaPlayer.setOnCompletionListener {
//////                                mediaPlayer.start()
//////                            }
////                            mediaPlayer.start()
////                        }
//
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                            mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
////                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
////                                .build())
////                        }
//
////                        mediaPlayer.start()
//
//
//                    } catch (e: IllegalArgumentException) {
//                        LogDebug.d("buffer: IllegalArgumentException" + e.message.valueOrEmpty())
//                    } catch (e: IllegalStateException) {
//                        LogDebug.d("buffer: IllegalStateException" + e.message.valueOrEmpty())
//                    } catch (e: IOException) {
//                        LogDebug.d("buffer: IOException" + e.message.valueOrEmpty())
//                    }
////
//                }
//            } catch (e: IOException) {
//                LogDebug.d(e.message.valueOrEmpty())
//            }
////
//        }
//
//
////        val readUtf8Line = buffer.readUtf8Line()
////        LogDebug.d("------------    $readUtf8Line")
////        testplayOggFile()
//
//
////        LogDebug.d("receive sendMessage: Play audio..... $data")
////        activity?.let { activity ->
////            try {
////                readUtf8Line?.let {
////                    val createTempFile = File.createTempFile("speechToText", "ogg", activity.cacheDir)
////                    createTempFile.deleteOnExit()
////                    val fileOutputStream = FileOutputStream(createTempFile)
////                    fileOutputStream.write(readUtf8Line.toByteArray())
////                    fileOutputStream.close()
////
////                    mediaPlayer.reset()
////                    val fileInputStream = FileInputStream(createTempFile)
////                    mediaPlayer.setDataSource(fileInputStream.fd)
////                    mediaPlayer.prepare()
////                    mediaPlayer.start()
////                }
////            } catch (e: IOException){
////                LogDebug.e("receive sendMessage: IOException.....", e)
////            }
////        }
//    }
//
//    private fun playListInputStream() {
//        try {
//            mediaPlayer?.let { mediaPlayer ->
//                if (lstAudio.isNotEmpty()) {
//                    val pathAudio = lstAudio.first()
//                    mediaPlayer.setDataSource(pathAudio)
//                    mediaPlayer.prepareAsync()
//                    mediaPlayer.setOnPreparedListener {
//                        mediaPlayer.start()
//                    }
//                    mediaPlayer.setOnCompletionListener {
//                        mediaPlayer.reset()
//                        lstAudio.removeAt(0)
//                        val file = File(pathAudio)
//                        if (file.exists()) {
//                            file.delete()
//                        }
//                        playListInputStream()
//                    }
//
//                }
//            }
//        } catch (e: IllegalArgumentException) {
//            LogDebug.d("buffer: IllegalArgumentException" + e.message.valueOrEmpty())
//        } catch (e: IllegalStateException) {
//            LogDebug.d("buffer: IllegalStateException" + e.message.valueOrEmpty())
//        } catch (e: IOException) {
//            LogDebug.d("buffer: IOException" + e.message.valueOrEmpty())
//        }
//
//    }
//
//    @SuppressLint("CheckResult")
//    private fun playFileOutputStream(fileOutputStream: File) {
//        val mBuffer = ByteArray(2048)
//        val mStreamAudioPlayer = StreamAudioPlayer.getInstance()
//        Observable.just<File>(fileOutputStream)
//            .subscribeOn(Schedulers.io())
//            .subscribe(
//                { file: File? ->
//                    try {
//                        mStreamAudioPlayer.init()
//                        val inputStream = FileInputStream(file)
//                        var read: Int
//                        while (inputStream.read(mBuffer).also { read = it } > 0) {
//                            mStreamAudioPlayer.play(mBuffer, read)
//                        }
//                        inputStream.close()
//                        mStreamAudioPlayer.release()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            ) { obj: Throwable -> obj.printStackTrace() }
//    }
//
//    override fun onPrepared(mp: MediaPlayer?) {
//        mp?.start()
//    }
//
//    private val playbackHandler = @SuppressLint("HandlerLeak")
//    object : Handler(){
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            when (msg.what) {
//                PlayerEvents.PLAYING_FAILED -> {
//                    LogDebug.d("The decoder failed to playback the file, check logs for more details");
//                }
//                PlayerEvents.PLAYING_FINISHED -> {
//                    LogDebug.d("The decoder finished successfully")
//                }
//                PlayerEvents.READING_HEADER -> {
//                    LogDebug.d("Starting to read header")
//                }
//
//                PlayerEvents.READY_TO_PLAY -> {
//                    LogDebug.d("READY to play - press play :)")
//                    player.play()
//                }
//                PlayerEvents.PLAY_UPDATE -> {
//                    LogDebug.d("Playing:" + (msg.arg1 / 60) + ":" + (msg.arg1 % 60) + " (" + (msg.arg1) + "s)")
//                }
//                PlayerEvents.TRACK_INFO -> {
//                    val bundle = msg.data
//                    bundle?.let {
//                        LogDebug.d(
//                            "title:" + it.getString("title") + " artist:" + it.getString("artist") + " album:" + it.getString(
//                                "album"
//                            ) +
//                                    " date:" + it.getString("date") + " track:" + it.getString("track")
//                        )
//                    }
//                }
//
//            }
//        }
//    }
//
//    var player: Player = Player(playbackHandler, Player.DecoderType.VORBIS)
//
//    private fun testplayOggFile(path: InputStream) {
//        Thread {
//            player.setDataSource(
//                path,
//                215
//            )
//        }.start()
//    }
//
//    private fun sendMessage(messageContent: String) {
//        LogDebug.d("sendMessage............. $messageContent")
//        view?.let { view ->
//            if (messageContent.isNotEmpty()) {
//                view.vgTyping.visible()
//                rvController.scrollToBottom()
//                val header = Header(
//                    namespace = ConstAPI.ChatBotHeaderNameSpace.SpeechRecognizer.value,
//                    name = ConstAPI.ChatBotHeaderName.Recognize.value,
//                    rawSpeech = messageContent,
//                    messageId = "${ConstAPI.messageIdKey}-${ConstApp.getMessageId()}",
//                    dialogRequestId = ConstApp.getMessageId()
//                )
//                val event = Event(header = header)
//                val metaStreamRequest = MetaStreamRequest(event = event)
//                presenter.sendMessage(meta = metaStreamRequest, isRecord = false)
//            } else {
//                view.vgTyping.gone()
//            }
//        }
//    }
//
//    private fun handleView(view: View) {
//        view.btnRetry.setOnClickListener {
//            if (doubleTouchPrevent.check("btnRetry")) {
//                presenter.getListMessage(isLoadMore = false)
//            }
//        }
//    }
//
//    private fun initView(view: View) {
//        val loadMoreConfig = RecyclerViewController.LoadMoreConfig(
//            isLoadMoreTop = true
//        ) {
//            if (presenter.isAllowLoadMore()) {
//                rvController.showLoadMore()
//                view.vgLoadMore.visible()
//                presenter.getListMessage(isLoadMore = true)
//            }
//        }
//        val input = LinearRenderConfigFactory.Input(
//            context = view.context,
//            orientation = LinearRenderConfigFactory.Orientation.VERTICAL,
//            loadMoreConfig = loadMoreConfig
//        )
//        val renderConfig = LinearRenderConfigFactory(
//            input
//        ).create()
//        rvController = RecyclerViewController(view.rvMessage, renderConfig)
//        rvController.addViewRenderer(HeaderMessageRenderer())
//        rvController.addViewRenderer(AudioMessageRenderer())
//        rvController.addViewRenderer(ImageMessageRenderer())
//        rvController.addViewRenderer(MusicMessageRenderer())
//        rvController.addViewRenderer(MyMessageRenderer())
//        rvController.addViewRenderer(RemindersRenderer { model, positionDay ->
//            if (!view.rvMessage.isComputingLayout) {
//                model.lstReminder.clear()
//                model.lstDay
//                loop@ for (day in model.lstDay) {
//                    if (day.isSelected) {
//                        day.isSelected = false
//                        break@loop
//                    }
//                }
//                if (model.lstDay.size > positionDay && positionDay >= 0) {
//                    model.lstReminder.addAll(model.lstDay[positionDay].reminders)
//                    model.lstDay[positionDay].isSelected = true
//                    rvController.notifyDataChanged()
//                }
//            }
//        }, ReminderRenderer())
//        rvController.addViewRenderer(TextMessageRenderer { model ->
//            val header = Header(
//                namespace = ConstAPI.ChatBotHeaderNameSpace.TextToSpeech.value,
//                name = ConstAPI.ChatBotHeaderName.StreamAudio.value,
//                messageId = model.messageId,
//                dialogRequestId = model.dialogRequestId
//            )
//            val payload = PayloadDevice(
//                text = model.message,
//                encodeFormat = ConstAPI.encodeFormatTextToSpeech,
//                language = ConstAPI.codeLanguageVn
//            )
//            val event = Event(header = header, payload = payload)
//            presenter.textToSpeech(MetaStreamRequest(event = event))
////            testplayOggFile("/data/user/0/com.olli.omni.demo/cache/test156095487521827377.ogg")
//            LogDebug.d("Start textToSpeech")
//        })
//        rvController.addViewRenderer(WeatherMessageRenderer())
//        rvController.addViewRenderer(BottomMessageRenderer())
//    }
//
//    override fun showListMessage(messages: List<MessageViewModel>, isLoadMore: Boolean) {
//        val lstData = MessageViewHolderModelMapper().map(messages)
//        if (lstData.isEmpty()) {
//            lstData.add(
//                0,
//                TextMessageViewHolderModel(
//                    isFirst = true,
//                    message = provider.getFirstMessageMaika(),
//                    url = "",
//                    dialogRequestId = "",
//                    messageId = ""
//                )
//            )
//        }
//        if (isLoadMore) {
//            rvController.addItems(0, lstData)
//            rvController.notifyDataChanged()
//            rvController.scrollToPosition(lstData.size)
//        } else {
//            rvController.setItems(lstData)
//            rvController.addItem(BottomMessageViewHolderModel())
//            rvController.notifyDataChanged()
//            rvController.scrollToBottom()
//        }
//        hideLoading()
//    }
//
//    override fun addNewMessage(lstMessage: List<MessageViewModel>) {
//        val lstData = MessageViewHolderModelMapper().map(lstMessage)
//        if (rvController.getNumItem() == 1) {
//            rvController.setItems(lstData)
//            rvController.addItem(BottomMessageViewHolderModel())
//        } else {
//            if (rvController.getNumItem() > 1) {
//                rvController.addItems(rvController.getNumItem() - 1, lstData)
//            }
//        }
//        rvController.notifyDataChanged()
//        rvController.scrollToBottom()
//        hideUIReplying()
//    }
//
//    override fun hideUIReplying() {
//        view?.vgTyping?.gone()
//    }
//
//    override fun showError(msgError: String) {
//        targetController?.let { targetController ->
//            appNotify.showError(router = targetController.router, msg = msgError)
//        }
//    }
//
//    override fun showViewRetryGetMessages() {
//        rvController.addItem(
//            TextMessageViewHolderModel(
//                isFirst = true,
//                message = provider.getFirstMessageMaika(),
//                url = "",
//                messageId = "",
//                dialogRequestId = ""
//
//            )
//        )
//        rvController.notifyDataChanged()
//    }
//
//    override fun showUIConnectChatBot() {
//        targetController?.let { targetController ->
//            if (targetController is HomeViewController) {
//                targetController.showUIConnectChatBot()
//            }
//        }
//    }
//
//    override fun showErrorConnectChatBotFail(msgError: String) {
//        showError(msgError)
//    }
//
//    override fun hideUIConnectChatBot() {
//        targetController?.let { targetController ->
//            if (targetController is HomeViewController) {
//                targetController.hideUIConnectChatBot()
//            }
//        }
//    }
//
//    override fun showLoading() {
//        view?.let { view ->
//            view.vgLoading.show()
//            view.vgViewRetryGetListMessage.gone()
//        }
//    }
//
//    override fun hideLoading() {
//        view?.let { view ->
//            view.vgLoading.hide()
//            view.vgLoadMore.gone()
//            rvController.hideLoadMore()
//        }
//    }
//
//    override fun onDestroyView(view: View) {
//        KBus.unsubscribe(this)
//        presenter.detachView()
//        threadPlayAudio?.interrupt()
//        super.onDestroyView(view)
//    }
//}