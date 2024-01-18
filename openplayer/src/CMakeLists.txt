# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#add_library( # Sets the name of the library.
#        libvorbis
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        )
add_library( # Sets the name of the library.
        vorbis-jni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ../jni/libogg/src/framing.c
        ../jni/libogg/src/bitwise.c
        ../jni/libvorbis/lib/block.c
        ../jni/libvorbis/lib/info.c
        ../jni/libvorbis/lib/synthesis.c
        ../jni/libvorbis/lib/registry.c
        ../jni/libvorbis/lib/codebook.c
        ../jni/libvorbis/lib/sharedbook.c
        ../jni/libvorbis/lib/psy.c
        ../jni/libvorbis/lib/window.c
        ../jni/libvorbis/lib/envelope.c
        ../jni/libvorbis/lib/mdct.c
        ../jni/libvorbis/lib/lpc.c
        ../jni/libvorbis/lib/smallft.c
        ../jni/libvorbis/lib/bitrate.c
        ../jni/libvorbis/lib/floor0.c
        ../jni/libvorbis/lib/floor1.c
        ../jni/libvorbis/lib/lsp.c
        ../jni/libvorbis/lib/res0.c
        ../jni/libvorbis/lib/mapping0.c
        ../jni/decodefeed/DecodeFeed.c
        ../jni/libvorbis-jni/org_xiph_vorbis_decoderjni_VorbisDecoder.c
        )

add_library( # Sets the name of the library.
        opus-jni
#
#        # Sets the library as a shared library.
        SHARED
#
        ../jni/libopus/src/analysis.c
        ../jni/libopus/src/mlp.c
        ../jni/libopus/src/mlp_data.c
        ../jni/libopus/src/opus.c
#        ../jni/libopus/src/opus_compare.c
        ../jni/libopus/src/opus_decoder.c
#        ../jni/libopus/src/opus_demo.c
        ../jni/libopus/src/opus_encoder.c
        ../jni/libopus/src/opus_header.c
        ../jni/libopus/src/opus_multistream.c
        ../jni/libopus/src/opus_multistream_decoder.c
        ../jni/libopus/src/opus_multistream_encoder.c
        ../jni/libopus/src/repacketizer.c
        ../jni/libopus/src/repacketizer_demo.c


        ../jni/libopus/celt/bands.c
        ../jni/libopus/celt/cwrs.c
        ../jni/libopus/celt/celt_decoder.c
        ../jni/libopus/celt/entdec.c
        ../jni/libopus/celt/kiss_fft.c
        ../jni/libopus/celt/mathops.c
        ../jni/libopus/celt/modes.c
        ../jni/libopus/celt/celt_lpc.c
        ../jni/libopus/celt/rate.c
        ../jni/libopus/celt/celt.c
        ../jni/libopus/celt/entcode.c
        ../jni/libopus/celt/celt_encoder.c
        ../jni/libopus/celt/entenc.c
        ../jni/libopus/celt/laplace.c
        ../jni/libopus/celt/mdct.c
        ../jni/libopus/celt/pitch.c
        ../jni/libopus/celt/quant_bands.c
        ../jni/libopus/celt/vq.c
#
        ../jni/libopus/silk/CNG.c
        ../jni/libopus/silk/code_signs.c
        ../jni/libopus/silk/init_decoder.c
        ../jni/libopus/silk/decode_core.c
        ../jni/libopus/silk/decode_frame.c
        ../jni/libopus/silk/decode_parameters.c
        ../jni/libopus/silk/decode_indices.c
        ../jni/libopus/silk/decode_pulses.c
        ../jni/libopus/silk/decoder_set_fs.c
        ../jni/libopus/silk/dec_API.c
        ../jni/libopus/silk/enc_API.c
        ../jni/libopus/silk/encode_indices.c
        ../jni/libopus/silk/encode_pulses.c
        ../jni/libopus/silk/gain_quant.c
        ../jni/libopus/silk/interpolate.c
        ../jni/libopus/silk/LP_variable_cutoff.c
        ../jni/libopus/silk/NLSF_decode.c
        ../jni/libopus/silk/NSQ.c
        ../jni/libopus/silk/NSQ_del_dec.c
        ../jni/libopus/silk/PLC.c
        ../jni/libopus/silk/shell_coder.c
        ../jni/libopus/silk/tables_gain.c
        ../jni/libopus/silk/tables_LTP.c
        ../jni/libopus/silk/tables_NLSF_CB_NB_MB.c
        ../jni/libopus/silk/tables_NLSF_CB_WB.c
        ../jni/libopus/silk/tables_other.c
        ../jni/libopus/silk/tables_pitch_lag.c
        ../jni/libopus/silk/tables_pulses_per_block.c
        ../jni/libopus/silk/VAD.c
        ../jni/libopus/silk/control_audio_bandwidth.c
        ../jni/libopus/silk/quant_LTP_gains.c
        ../jni/libopus/silk/VQ_WMat_EC.c
        ../jni/libopus/silk/HP_variable_cutoff.c
        ../jni/libopus/silk/NLSF_encode.c
        ../jni/libopus/silk/NLSF_VQ.c
        ../jni/libopus/silk/NLSF_unpack.c
        ../jni/libopus/silk/NLSF_del_dec_quant.c
        ../jni/libopus/silk/process_NLSFs.c
        ../jni/libopus/silk/stereo_LR_to_MS.c
        ../jni/libopus/silk/stereo_MS_to_LR.c
        ../jni/libopus/silk/check_control_input.c
        ../jni/libopus/silk/control_SNR.c
        ../jni/libopus/silk/init_encoder.c
        ../jni/libopus/silk/control_codec.c
        ../jni/libopus/silk/A2NLSF.c
        ../jni/libopus/silk/ana_filt_bank_1.c
        ../jni/libopus/silk/biquad_alt.c
        ../jni/libopus/silk/bwexpander_32.c
        ../jni/libopus/silk/bwexpander.c
        ../jni/libopus/silk/debug.c
        ../jni/libopus/silk/decode_pitch.c
        ../jni/libopus/silk/inner_prod_aligned.c
        ../jni/libopus/silk/lin2log.c
        ../jni/libopus/silk/log2lin.c
        ../jni/libopus/silk/LPC_analysis_filter.c
        ../jni/libopus/silk/LPC_inv_pred_gain.c
        ../jni/libopus/silk/table_LSF_cos.c
        ../jni/libopus/silk/NLSF2A.c
        ../jni/libopus/silk/NLSF_stabilize.c
        ../jni/libopus/silk/NLSF_VQ_weights_laroia.c
        ../jni/libopus/silk/pitch_est_tables.c
        ../jni/libopus/silk/resampler.c
        ../jni/libopus/silk/resampler_down2_3.c
        ../jni/libopus/silk/resampler_down2.c
        ../jni/libopus/silk/resampler_private_AR2.c
        ../jni/libopus/silk/resampler_private_down_FIR.c
        ../jni/libopus/silk/resampler_private_IIR_FIR.c
        ../jni/libopus/silk/resampler_private_up2_HQ.c
        ../jni/libopus/silk/resampler_rom.c
        ../jni/libopus/silk/sigm_Q15.c
        ../jni/libopus/silk/sort.c
        ../jni/libopus/silk/sum_sqr_shift.c
        ../jni/libopus/silk/stereo_decode_pred.c
        ../jni/libopus/silk/stereo_encode_pred.c
        ../jni/libopus/silk/stereo_find_predictor.c
        ../jni/libopus/silk/stereo_quant_pred.c
        ../jni/libopus/silk/fixed/LTP_analysis_filter_FIX.c
        ../jni/libopus/silk/fixed/LTP_scale_ctrl_FIX.c
        ../jni/libopus/silk/fixed/corrMatrix_FIX.c
        ../jni/libopus/silk/fixed/encode_frame_FIX.c
        ../jni/libopus/silk/fixed/find_LPC_FIX.c
        ../jni/libopus/silk/fixed/find_LTP_FIX.c
        ../jni/libopus/silk/fixed/find_pitch_lags_FIX.c
        ../jni/libopus/silk/fixed/find_pred_coefs_FIX.c
        ../jni/libopus/silk/fixed/noise_shape_analysis_FIX.c
        ../jni/libopus/silk/fixed/prefilter_FIX.c
        ../jni/libopus/silk/fixed/process_gains_FIX.c
        ../jni/libopus/silk/fixed/regularize_correlations_FIX.c
        ../jni/libopus/silk/fixed/residual_energy16_FIX.c
        ../jni/libopus/silk/fixed/residual_energy_FIX.c
        ../jni/libopus/silk/fixed/solve_LS_FIX.c
        ../jni/libopus/silk/fixed/warped_autocorrelation_FIX.c
        ../jni/libopus/silk/fixed/apply_sine_window_FIX.c
        ../jni/libopus/silk/fixed/autocorr_FIX.c
        ../jni/libopus/silk/fixed/burg_modified_FIX.c
        ../jni/libopus/silk/fixed/k2a_FIX.c
        ../jni/libopus/silk/fixed/k2a_Q16_FIX.c
        ../jni/libopus/silk/fixed/pitch_analysis_core_FIX.c
        ../jni/libopus/silk/fixed/vector_ops_FIX.c
        ../jni/libopus/silk/fixed/schur64_FIX.c
        ../jni/libopus/silk/fixed/schur_FIX.c
        ../jni/libogg/src/framing.c
        ../jni/decodefeed/DecodeFeed.c
        ../jni/libopus-jni/OpusHeader.c
        ../jni/libopus-jni/org_xiph_opus_decoderjni_OpusDecoder.c
        )
include_directories(../jni/libogg/include)

include_directories(../jni/libopus/celt)
include_directories(../jni/libopus/celt/arm)
include_directories(../jni/libopus/celt/x86)

include_directories(../jni/libopus)
include_directories(../jni/libopus/include)

include_directories(../jni/libopus/silk)
include_directories(../jni/libopus/silk/arm)
include_directories(../jni/libopus/silk/fixed)
include_directories(../jni/libopus/silk/float)

include_directories(../jni/libopus/src)
include_directories(../jni/libopus-jni)

include_directories(../jni/libvorbis/include)
include_directories(../jni/libvorbis-jni)

include_directories(../jni/decodefeed)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
#
target_link_libraries( # Specifies the target library.
        vorbis-jni

        ${log-lib})
target_link_libraries( # Specifies the target library.
        opus-jni

        ${log-lib})