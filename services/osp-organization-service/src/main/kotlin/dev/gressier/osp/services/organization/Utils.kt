package dev.gressier.osp.services.organization

import mu.KotlinLogging
import java.util.concurrent.TimeoutException

private val log = KotlinLogging.logger {}

fun timeOutOnceIn(n: Int) {
    if ((1..n).random() == 1) {
        try {
            Thread.sleep(2_000)
            throw TimeoutException()
        } catch (e: InterruptedException) {
            log.error(e.message)
        }
    }
}