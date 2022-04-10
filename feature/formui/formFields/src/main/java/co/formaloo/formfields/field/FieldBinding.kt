package co.formaloo.formfields.field

import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import co.formaloo.common.Constants
import co.formaloo.common.Constants.SECTION
import co.formaloo.common.Constants.like_dislike
import co.formaloo.common.Constants.national_number
import co.formaloo.common.Constants.nps
import co.formaloo.common.Constants.star
import co.formaloo.model.KeyValueModel
import co.formaloo.model.form.Fields
import co.formaloo.formfields.KeyValueSpinnerAdapter
import co.formaloo.formfields.R
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber


object FieldBinding {

    @BindingAdapter("app:items", "app:selectedItem")
    @JvmStatic
    fun setKeyValueItems(
        spinner: AppCompatSpinner,
        items: ArrayList<KeyValueModel>,
        selected: String?
    ) {

        if (spinner.adapter is KeyValueSpinnerAdapter)
            with(spinner.adapter as KeyValueSpinnerAdapter) {

                listItemsTxt = items

                Timber.e("selected $selected")
                Timber.e("items $items")

                for (item in items) {
                    if (item.sign.equals(selected)) {
                        spinner.setSelection(items.indexOf(item))

                    }
                }

            }

    }

    @BindingAdapter("app:inttext")
    @JvmStatic
    fun textValue(txv: TextInputEditText, value: Int?) {
        value?.let {
            txv.setText("$value")
        }
    }

    @BindingAdapter("app:fieldTypeTxt")
    @JvmStatic
    fun fieldTypeTxt(txv: TextView, fields: Fields?) {
        fields?.type?.let { type ->
            val cxt = txv.context

            txv.text = when (type) {
                Constants.FILE -> {
                    cxt.getString(R.string.FILE)
                }
                Constants.DROPDOWN -> {
                    cxt.getString(R.string.DROPDOWN)
                }
                Constants.CITY -> {
                    cxt.getString(R.string.city)
                }
                Constants.LOCATION -> {
                    cxt.getString(R.string.location)
                }
                Constants.RATING -> {

                    if (fields.rating_type != null) {
                        when (fields.rating_type) {
                            star -> {
                                cxt.getString(R.string.star_)
                            }
                            nps -> {
                                cxt.getString(R.string.nps_)
                            }
                            like_dislike -> {
                                cxt.getString(R.string.like_dislike_)
                            }
                            else -> {
                                cxt.getString(R.string.RATING)
                            }
                        }
                    } else {
                        cxt.getString(R.string.RATING)
                    }
                }
                Constants.EMAIL -> {
                    cxt.getString(R.string.EMAIL)
                }
                Constants.SHORT_TEXT -> {
                    val jsonKey = fields.json_key
                    if (jsonKey != null && jsonKey is String && jsonKey.equals(national_number)) {
                        cxt.getString(R.string.national_number)
                    } else {
                        cxt.getString(R.string.SHORT_TEXT)

                    }

                }
                Constants.LONG_TEXT -> {
                    cxt.getString(R.string.LONG_TEXT)
                }
                Constants.TIME -> {
                    cxt.getString(R.string.TIME)
                }
                Constants.MONEY -> {
                    cxt.getString(R.string.MONEY)
                }
                Constants.WEBSITE -> {
                    cxt.getString(R.string.WEBSITE)
                }
                Constants.PHONE -> {
                    cxt.getString(R.string.PHONE)
                }
                Constants.YESNO -> {
                    cxt.getString(R.string.YESNO)
                }
                Constants.MULTI_SELECT -> {
                    cxt.getString(R.string.MULTI_SELECT)
                }
                Constants.SINGLE_SELECT -> {
                    cxt.getString(R.string.SINGLE_SELECT)
                }
                Constants.NUMBER -> {
                    cxt.getString(R.string.NUMBER)
                }
                Constants.DATE -> {
                    cxt.getString(R.string.DATE)
                }
                Constants.MATRIX -> {
                    cxt.getString(R.string.MATRIX)
                }
                Constants.SECTION -> {
                    cxt.getString(R.string.SECTION)
                }
                Constants.phone_verification -> {
                    cxt.getString(R.string.phone_verification)
                }
                Constants.META -> {
                    if (fields.sub_type != null) {
                        when (fields.sub_type) {
                            SECTION -> {
                                cxt.getString(R.string.SECTION)
                            }
                            else -> {
                                cxt.getString(R.string.META)
                            }
                        }
                    } else {
                        cxt.getString(R.string.META)
                    }

                }
                Constants.SIGNATURE -> {
                    cxt.getString(R.string.signature_title_mobile)
                }
                else -> {
                    ""
                }
            }
        }
    }

//    @BindingAdapter("app:items")
//    @JvmStatic
//    fun setCpulonnItems(dragListView: DragListView, items: List<ChoiceItem>?) {
//
//        if (dragListView.adapter is CulomnsAdapter)
//            with(dragListView.adapter as CulomnsAdapter) {
//
//                val mItemsArray = arrayListOf<Pair<Long, ChoiceItem>>()
//                items?.let {
//                    for (i in items.indices) {
//                        mItemsArray.add(Pair(i.toLong(), items[i]))
//                    }
//                }
//
//                itemList = mItemsArray
//
//            }
//
//    }


}
