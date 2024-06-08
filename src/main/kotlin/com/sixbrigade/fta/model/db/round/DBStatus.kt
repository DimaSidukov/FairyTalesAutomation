package com.sixbrigade.fta.model.db.round

enum class DBStatus(val code: Int) {
    NOT_STARTED(0),
    STARTED(1),
    AWAIT_FIRST_WONDER_APPROVAL(2),
    FIRST_WONDER_APPROVED(3),
    FIRST_WONDER_REJECTED(4),
    AWAIT_SECOND_WONDER_APPROVAL(5),
    SECOND_WONDER_APPROVED(6),
    SECOND_WONDER_REJECTED(7),
    AWAIT_THIRD_WONDER_APPROVAL(8),
    THIRD_WONDER_APPROVED(9),
    THIRD_WONDER_REJECTED(10),
    AWAIT_LAST_WONDER_APPROVAL(11),
    LAST_WONDER_APPROVED(12),
    LAST_WONDER_REJECTED(13),
    FINISHED(14)
}