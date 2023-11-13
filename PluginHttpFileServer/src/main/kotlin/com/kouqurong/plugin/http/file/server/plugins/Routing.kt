/*
 * Copyright 2023 The Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kouqurong.plugin.http.file.server.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
  routing {
    get("/") { call.respondText("Hello World!") }
    // Static plugin. Try to access `/static/index.html`
    static("/static") { resources("static") }
  }
}

fun Application.configFiles(path: String, file: File) {
  routing {
    get(path) {
      val rsp = buildString {
        append(
            """
                    <!DOCTYPE html>
                    <html>
                        <head>
                            <meta name="viewport" content="width=device-width,initial-scale=1" />
                            <style>
                                .container {
                                    display: grid;
                                    grid-auto-rows: 1fr;
                                    grid-template-columns: 1fr 1fr 1fr;
                                    justify-content: center;
                                    align-items: center;
                                }
                                
                                @media screen and (max-width: 720px){
                                  .container {
                                        display: flex;
                                        justify-content: center;
                                        align-items: center;
                                        flex-direction: column;
                                  }
                                }
                               
                                 a {
                                    padding: 10px;
                                    margin: 20px;
                                    color: white;
                                    display: flex;
                                    justify-content: center;
                                    align-items: center;
                                    gap: 20px;
                                    border-radius: 79px;
                                    backdrop-filter: blur(20px);
                                    background-color: rgba(4,0,255, 0.43);
                                    box-shadow: rgba(0, 0, 0, 0.3) 2px 8px 8px;
                                    border: 0px rgba(255,255,255,0.4) solid;
                                    border-bottom: 0px rgba(40,40,40,0.35) solid;
                                    border-right: 0px rgba(40,40,40,0.35) solid;
                                }
                            </style>
                        </head>
                        <body>
                """
                .trimIndent())

        append("""<div class="container" id="container">""")

        file.walk().forEach { append(""" <a href="${it.name}">${it.name}</a>""") }

        append("""</div>""")

        append(
            """
                        </body>
                    </html>
                """
                .trimIndent())
      }

      call.respondText(rsp, io.ktor.http.ContentType.Text.Html)
    }
    static {}
    staticFiles(path, file)
  }
}
