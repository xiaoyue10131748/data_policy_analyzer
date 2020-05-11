/*
  Copyright 2019 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.sdk.sample.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.kakao.sdk.sample.common.databinding.FragmentDebugBinding
import io.reactivex.Observable

/**
 * @author kevin.kang. Created on 2019-10-17..
 */
class DebugFragment : AppCompatDialogFragment() {

    private lateinit var logsObservable: Observable<Any>
    private lateinit var binding: FragmentDebugBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDebugBinding.inflate(inflater, container, false)
        binding.logs =
            """
                ==== app version: ${BuildConfig.VERSION_NAME}
                ==== sdk version: ${com.kakao.sdk.common.BuildConfig.VERSION_NAME}
                
            """.trimIndent()
        logsObservable.doOnNext {
            binding.logs += "$it\n"
        }.subscribe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }


    companion object {
        fun newInstance(replay: Observable<Any>) = DebugFragment().apply {
            logsObservable = replay
        }
    }
}