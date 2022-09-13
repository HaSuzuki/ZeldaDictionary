package com.suzukiha.zeldadictionary.util


const val QUERY_LIMIT = 10

/**
 * firestore where-in queryのlimitが10なのでlistをslice
 */
fun sliceLimitList(list: ArrayList<Int>): ArrayList<List<Int>> {
    val size = list.size
    val requestCount = size / QUERY_LIMIT
    val surplus = size % QUERY_LIMIT
    val takeList = arrayListOf<List<Int>>()
    (1..requestCount).forEach { i ->
        val tmpList = list.drop((QUERY_LIMIT * i) - QUERY_LIMIT)
        takeList.add(tmpList.take(QUERY_LIMIT))

    }
    if (surplus != 0) {
        takeList.add(list.takeLast(surplus))
    }
    return takeList
}