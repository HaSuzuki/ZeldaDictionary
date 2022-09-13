package com.suzukiha.zeldadictionary

import com.suzukiha.zeldadictionary.util.sliceLimitList
import org.junit.Assert
import org.junit.Test


class ListExtTest {

    @Test
    fun sliceLimitList_surplus() {
        val list = ArrayList((1..22).toList())
        val actualList = sliceLimitList(list = list)
        val expectedList = arrayListOf<List<Int>>(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
            listOf(21, 22)
        )
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun sliceLimitList_surplus2() {
        val list = ArrayList((1..19).toList())

        val actualList = sliceLimitList(list = list)
        val expectedList = arrayListOf<List<Int>>(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            listOf(11, 12, 13, 14, 15, 16, 17, 18, 19)
        )
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun sliceLimitList_divisible() {
        val list = ArrayList((1..30).toList())

        val actualList = sliceLimitList(list = list)
        val expectedList = arrayListOf<List<Int>>(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
            listOf(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
        )
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun sliceLimitList_random() {
        val list = arrayListOf<Int>(1, 5, 20, 2, 6, 3)

        val actualList = sliceLimitList(list = list)
        val expectedList = arrayListOf<List<Int>>(
            listOf(1, 5, 20, 2, 6, 3)
        )
        Assert.assertEquals(expectedList, actualList)
    }

    @Test
    fun sliceLimitList_random2() {
        val list = arrayListOf<Int>(-1, 5, 20, 2, 6, 3)

        val actualList = sliceLimitList(list = list)
        val expectedList = arrayListOf<List<Int>>(
            listOf(-1, 5, 20, 2, 6, 3)
        )
        Assert.assertEquals(expectedList, actualList)
    }
}