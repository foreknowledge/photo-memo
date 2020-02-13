package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

data class Memo(val title: String, val content: String, val thumbnail: List<Bitmap>? = null)

fun getSampleMemo(context: Context): List<Memo> {
    val images1 = listOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_01),
        BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_02)
    )

    val images2 = listOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_03),
        BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_04),
        BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_05)
    )

    return listOf(
        Memo("제목1", "본문1 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼", images1),
        Memo("제목2", "본문2 아머리아ㅓㄹ미나ㅓㄹ미;", images2),
        Memo("제목3", "본문3 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼"),
        Memo("제목4", "본문4 아머리아ㅓㄹ미나ㅓㄹ미;")
    )
}