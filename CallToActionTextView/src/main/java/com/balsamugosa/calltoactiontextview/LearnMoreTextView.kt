package com.balsamugosa.calltoactiontextview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class LearnMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.learnMoreTextView
) : AppCompatTextView(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var mCollapsedLines: Int? = ZERO
    private var mActionText: CharSequence = LEARN_MORE
    private var foregroundColor: Int? = ZERO
    private var initialText: String? = BLANK
    private var isUnderlined: Boolean? = false
    private var isBoldActionText: Boolean? = false
    private var mActionTextColor: Int? = ZERO
    var learnMoreTextViewOnClickListener : LearnMoreTextViewOnClickListener? = null

    private lateinit var visibleText: String

    override fun onClick(v: View?) {
        click()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (initialText.isNullOrBlank()) {
            if (lineCount > mCollapsedLines!!) {
                initialText = text.toString()
                visibleText = visibleText()

                setOnClickListener(this)
            }
        }
        setEllipsizedText()
    }

    private fun click() {
        learnMoreTextViewOnClickListener?.onActionTextClick()
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LearnMoreTextView).apply {
            try {
                mCollapsedLines = getInt(R.styleable.LearnMoreTextView_collapsedLines, COLLAPSED_MAX_LINES)
                mActionText = getString(R.styleable.LearnMoreTextView_actionText) ?: LEARN_MORE
                foregroundColor = getColor(R.styleable.LearnMoreTextView_foregroundColor, Color.TRANSPARENT)
                isUnderlined = getBoolean(R.styleable.LearnMoreTextView_isActionUnderlined, false)
                isBoldActionText = getBoolean(R.styleable.LearnMoreTextView_isBoldActionText, false)
                mActionTextColor = getColor(R.styleable.LearnMoreTextView_actionTextColor, Color.BLUE)
            } finally {
                this.recycle()
            }
        }

        maxLines = mCollapsedLines!!
    }

    private fun setEllipsizedText() {
        if (initialText?.isBlank()!!) {
            return
        }

        text = SpannableStringBuilder(
            visibleText.substring(ZERO,
                visibleText.length - (mActionText.toString().length + DEFAULT_ELLIPSIZED_TEXT.length)))
            .append(DEFAULT_ELLIPSIZED_TEXT)
            .append(mActionText.toString().span())
    }

    private fun visibleText(): String {
        var end = ZERO

        return if (mCollapsedLines!! < COLLAPSED_MAX_LINES) {
            for (i in ZERO until mCollapsedLines!!) {
                if (layout.getLineEnd(i) == ZERO)
                    break
                else
                    end = layout.getLineEnd(i)
            }
            initialText?.substring(ZERO, end - mActionText.toString().length)!!
        } else {
            initialText!!
        }


    }

    private fun String.span(): SpannableString =
        SpannableString(this).apply {
            setSpan(
                ForegroundColorSpan(mActionTextColor!!),
                ZERO,
                this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (isUnderlined!!)
                setSpan(
                    UnderlineSpan(),
                    ZERO,
                    this.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            if (isBoldActionText!!)
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    ZERO,
                    this.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        }

    companion object {

        const val ZERO = 0
        const val BLANK = ""
        const val COLLAPSED_MAX_LINES = Int.MAX_VALUE
        const val LEARN_MORE = "Learn More"
        const val DEFAULT_ELLIPSIZED_TEXT = "... "
    }
}

interface LearnMoreTextViewOnClickListener {
    fun onActionTextClick()
}