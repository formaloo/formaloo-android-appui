package co.formaloo.formfields.field

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.formaloo.formfields.R

enum class CSATFunny(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.angry, R.drawable.csat_star),
    SAD(R.string.sad, R.drawable.csat_star),
    NEUTRAL(R.string.neutral, R.drawable.csat_star),
    SMILE(R.string.smile, R.drawable.csat_star),
    LOVE(R.string.love, R.drawable.csat_star)
}

enum class CSATFlat(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.angry, R.drawable.csat_star),
    SAD(R.string.sad, R.drawable.csat_star),
    NEUTRAL(R.string.neutral, R.drawable.csat_star),
    SMILE(R.string.smile, R.drawable.csat_star),
    LOVE(R.string.love, R.drawable.csat_star)
}

enum class CSATMonster(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.angry, R.drawable.csat_star),
    SAD(R.string.sad, R.drawable.csat_star),
    NEUTRAL(R.string.neutral, R.drawable.csat_star),
    SMILE(R.string.smile, R.drawable.csat_star),
    LOVE(R.string.love, R.drawable.csat_star)
}

enum class CSATOutline(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.angry, R.drawable.csat_star),
    SAD(R.string.sad, R.drawable.csat_star),
    NEUTRAL(R.string.neutral, R.drawable.csat_star),
    SMILE(R.string.smile, R.drawable.csat_star),
    LOVE(R.string.love, R.drawable.csat_star)
}

enum class CSATHEART(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.empty_txt, R.drawable.csat_star),
    SAD(R.string.empty_txt, R.drawable.csat_star),
    NEUTRAL(R.string.empty_txt, R.drawable.csat_star),
    SMILE(R.string.empty_txt, R.drawable.csat_star),
    LOVE(R.string.empty_txt, R.drawable.csat_star)
}

enum class CSATSTAR(@StringRes val str: Int, @DrawableRes val drawable: Int) {
    ANGRY(R.string.empty_txt, R.drawable.csat_star),
    SAD(R.string.empty_txt, R.drawable.csat_star),
    NEUTRAL(R.string.empty_txt, R.drawable.csat_star),
    SMILE(R.string.empty_txt, R.drawable.csat_star),
    LOVE(R.string.empty_txt, R.drawable.csat_star)
}
