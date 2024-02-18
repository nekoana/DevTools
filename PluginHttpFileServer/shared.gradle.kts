/*
 * Copyright 2024 The Open Source Project
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
val osName = System.getProperty("os.name").lowercase()

val isWindows = osName.contains("win")
val isMac = osName.contains("mac")
val isLinux = osName.contains("linux")

val fileExtension = if (isWindows) ".exe" else if (isLinux) ".so" else ""
val fileSeparator = if (isWindows) "\\" else "/"

val fileName = "ShareFileServer" + fileExtension

val copyCommand = if (isWindows) "copy" else "cp"


val commands = buildList<String> {
    if (isWindows) {
        add("cmd")
        add("/c")
    } else {
        add("sh")
        add("-c")
    }

    // 检查是否已经有 ShareFileServer 文件夹，如果没有则克隆仓库
    add(
        """
       if [ ! -d "ShareFileServer" ]; then
            git clone https://github.com/nekoana/ShareFileServer.git
       else
            cd ShareFileServer && git pull && cd ..
       fi &&
       cd ShareFileServer && cargo build --release &&
       $copyCommand target/release/$fileName ../src/main/resources/$fileName &&
       cd .. && rm -rf ShareFileServer
       """
    )
}

val shareFileServerExists = File(project.projectDir, "src/main/resources/$fileName").exists()

if(!shareFileServerExists) {
    tasks.register<Exec>(
        "fetchAndBuildShareFileServer"
    ) {
        // 指定要执行的命令
        commandLine = commands
        // 指定任务执行的工作目录
        workingDir = project.projectDir
        // 任务执行完成后的操作
        doLast {
            println("Task fetchAndBuildShareFileServer completed")
        }
    }

    tasks.getByName("build").dependsOn("fetchAndBuildShareFileServer")
}


