package com.thomas.spring.boot.context

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ContextSpringBootApplication

fun main(args: Array<String>) {
    runApplication<ContextSpringBootApplication>(*args)
}
