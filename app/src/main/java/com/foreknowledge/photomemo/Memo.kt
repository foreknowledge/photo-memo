package com.foreknowledge.photomemo

import android.graphics.Bitmap

data class Memo(val title: String, val content: String, val thumbnail: List<Bitmap>? = null)

fun getSampleMemo(): List<Memo> {
    return listOf(
        Memo("제목1", "본문1 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼"),
        Memo("제목2", "본문2 아머리아ㅓㄹ미나ㅓㄹ미;"),
        Memo("제목3", "본문3 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼 아머리아ㅓㄹ미나ㅓㄹ미;ㅏㄷ저림나ㅓ리;ㅏㄴ머이ㅏㄹ먼"),
        Memo("제목4", "본문4 아머리아ㅓㄹ미나ㅓㄹ미;")
    )
}