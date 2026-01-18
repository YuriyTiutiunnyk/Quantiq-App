package com.example.quantiq.data.mapper

import com.example.quantiq.data.CounterEntity
import com.example.quantiq.domain.model.Counter

fun CounterEntity.toDomain(): Counter =
    Counter(
        id = id,
        title = title,
        value = value,
        step = step,
        isLocked = isLocked
    )

fun Counter.toEntity(): CounterEntity =
    CounterEntity(
        id = id,
        title = title,
        value = value,
        step = step,
        isLocked = isLocked
    )
