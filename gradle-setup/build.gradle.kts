import com.pswidersk.gradle.python.VenvTask

plugins {
    id("com.pswidersk.python-plugin") version "2.8.2"
}

pythonPlugin {
    pythonVersion = "3.12.8"
}

tasks {
    val containerName = "ai-assistant-container"
    val imageName = "ai-assistant-img"
//    val aiModel = "deepseek-r1:1.5b"
    val aiModel = "deepseek-r1"

    register<Exec>("buildImage") {
        workingDir = projectDir.parentFile
        commandLine("docker", "build", "-t", imageName, "-f", "Dockerfile", ".")
    }

    register<Exec>("runAssistContainer") {
        commandLine(
            "docker",
            "run",
//            "--gpus=all",
            "-e", "OLLAMA_GPU_OVERHEAD=2443348019",
            "-e", "AI_MODEL=$aiModel",
            "-e", "OLLAMA_KEEP_ALIVE=24h",
            "-v", "ollama-cache:/root/.cache",
            "-v", "ollama:/root/.ollama",
            "--name",
            containerName,
            "--rm",
            imageName,
            "test-project/sample_script.py",
            "Replace any 'world' string in the code  with 'from your AI Assistant :)' string."
        )
        dependsOn("buildImage")
    }

    register<Exec>("stopAssistContainer") {
        workingDir = projectDir.parentFile
        commandLine("docker", "rm", "-f", containerName)
    }

    val pipInstall by registering(VenvTask::class) {
        venvExec = "pip"
        workingDir = projectDir.parentFile
        args = listOf("install", "--isolated", "-e", ".")
    }

    register<VenvTask>("runApiServer") {
        workingDir = projectDir.parentFile
        args = listOf("demo/fastapi_app.py")
        dependsOn(pipInstall)
    }

    register<VenvTask>("runApiClient") {
        workingDir = projectDir.parentFile
        args = listOf("demo/fastapi_client.py")
        dependsOn(pipInstall)
    }
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

