/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.content

/**
 * Create by ndn
 * Create on 4/24/20
 * com.halo.content
 *
 * - text(@StringRes resId: Int): CharSequence
 * - textArray(@ArrayRes resId: Int): Array<CharSequence>
 * - quantityText(@PluralsRes resId: Int, quantity: Int): CharSequence
 * - string(@StringRes resId: Int): String
 * - string(@StringRes resId: Int, vararg formatArgs: Any): String
 * - stringArray(@ArrayRes resId: Int): Array<String>
 * - quantityString(@PluralsRes resId: Int, quantity: Int): String
 * - quantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
 * - integer(@IntegerRes resId: Int): Int
 * - intArray(@ArrayRes resId: Int): IntArray
 * - boolean(@BoolRes resId: Int): Boolean
 * - dimension(@DimenRes resId: Int): Float
 * - dimensionPixelSize(@DimenRes resId: Int): Int
 * - dimensionPixelOffset(@DimenRes resId: Int): Int
 * - drawable(@DrawableRes resId: Int): Drawable?
 * - drawable(@DrawableRes id: Int, @StyleRes themeResId: Int): Drawable?
 * - drawableForDensity(@DrawableRes id: Int, @StyleRes themeResId: Int, density: Int): Drawable?
 * - color(@ColorRes resId: Int): Int
 * - color(@AttrRes attrResId: Int, @StyleRes themeResId: Int): Int
 * - colorRes(@AttrRes attrResId: Int, @StyleRes themeResId: Int): Int
 * - colorStateList(@ColorRes resId: Int): ColorStateList?
 * - colorStateList(@ColorRes id: Int, @StyleRes themeResId: Int): ColorStateList?
 * - colorStateListFromAttr(@AttrRes attrResId: Int, @StyleRes themeResId: Int): ColorStateList
 * - font(@FontRes id: Int): Typeface?
 * - loadAnimation(@AnimRes id: Int): Animation
 * - resolveAttribute(@AttrRes id: Int, outValue: TypedValue, resolveRefs: Boolean): Boolean
 * - resolveAttribute(@AttrRes id: Int, @StyleRes themeResId: Int, outValue: TypedValue, resolveRefs: Boolean): Boolean
 * - value(@DimenRes id: Int, resolveRefs: Boolean): TypedValue
 * - identifier(name: String, defType: String, defPackage: String): Int
 *
 * Themes
 * You can also easily change themed attributes as follow:
 *
 * by @ColorRes:
 *
 * .setBackgroundColor(resourcesProvider.colorRes(R.attr.colorPrimary, R.style.App_Style_A)
 * by @ColorInt:
 *
 * .setColor(resourcesProvider.color(R.attr.colorPrimary, R.style.App_Style_B))
 * by ColorStateList:
 *
 * .backgroundTintList = resourcesProvider.colorStateListFromAttr(R.attr.colorPrimary, R.style.App_Style_C)
 * .backgroundTintList = resourcesProvider.colorStateList(R.color.color_selector, R.style.App_Style_C)
 * Sample usage
 * By Kotlin Extensions (View, Activity, Fragment):
 *
 * class Activity : AppCompatActivity(){
 * fun work() {
 * resourcesProvider().string(R.string.app_name_label)
 * }
 * }
 * By Dependency Injection (ex: Dagger):
 * Add to your dependencies graph:
 *
 * @JvmStatic
 * @Provides
 * @Singleton
 * fun provideResourcesProvider(context: YourApp): ResourcesProvider{
 * return ResourcesProvider(context)
 * }
 * Use it:
 *
 * //by constructor injection
 * class MyClass @Inject constructor(resourcesProvider: ResourcesProvider)
 *
 * //by field injection
 * @Inject
 * protected lateinit var resourcesProvider: ResourcesProvider
 *
 * fun work(){
 * resourcesProvider.color(R.color.color_1)
 * }
 */

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

class ResourcesProvider
constructor(val ctx: Context) {

    constructor(ctx: Context, @StyleRes themeResId: Int) : this(ctx) {
        ctx.setTheme(themeResId)
    }

    inline fun text(@StringRes id: Int): CharSequence = ctx.getText(id)

    @Throws(Resources.NotFoundException::class)
    inline fun textArray(@ArrayRes id: Int): Array<CharSequence> = ctx.resources.getTextArray(id)

    @Throws(Resources.NotFoundException::class)
    inline fun quantityText(@PluralsRes id: Int, quantity: Int): CharSequence =
        ctx.resources.getQuantityText(id, quantity)

    inline fun string(@StringRes id: Int): String = ctx.getString(id)

    inline fun string(@StringRes id: Int, vararg formatArgs: Any): String =
        ctx.getString(id, *formatArgs)

    @Throws(Resources.NotFoundException::class)
    inline fun stringArray(@ArrayRes id: Int): Array<String> = ctx.resources.getStringArray(id)

    @Throws(Resources.NotFoundException::class)
    inline fun quantityString(@PluralsRes id: Int, quantity: Int): String =
        ctx.resources.getQuantityString(id, quantity)

    @Throws(Resources.NotFoundException::class)
    inline fun quantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String =
        ctx.resources.getQuantityString(id, quantity, *formatArgs)

    @Throws(Resources.NotFoundException::class)
    inline fun integer(@IntegerRes id: Int): Int = ctx.resources.getInteger(id)

    @Throws(Resources.NotFoundException::class)
    inline fun intArray(@ArrayRes id: Int): IntArray = ctx.resources.getIntArray(id)

    @Throws(Resources.NotFoundException::class)
    inline fun boolean(@BoolRes id: Int): Boolean = ctx.resources.getBoolean(id)

    @Throws(Resources.NotFoundException::class)
    inline fun dimension(@DimenRes id: Int): Float = ctx.resources.getDimension(id)

    @Throws(Resources.NotFoundException::class)
    inline fun dimensionPixelSize(@DimenRes id: Int): Int = ctx.resources.getDimensionPixelSize(id)

    @Throws(Resources.NotFoundException::class)
    inline fun dimensionPixelOffset(@DimenRes id: Int): Int =
        ctx.resources.getDimensionPixelOffset(id)

    inline fun drawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(ctx, id)

    inline fun drawable(@DrawableRes id: Int, @StyleRes themeResId: Int): Drawable? =
        ResourcesCompat.getDrawable(ctx.resources, id, ContextThemeWrapper(ctx, themeResId).theme)

    inline fun drawableForDensity(
        @DrawableRes id: Int,
        @StyleRes themeResId: Int,
        density: Int
    ): Drawable? =
        ResourcesCompat.getDrawableForDensity(
            ctx.resources,
            id,
            density,
            ContextThemeWrapper(ctx, themeResId).theme
        )

    @ColorInt
    inline fun color(@ColorRes id: Int): Int = ContextCompat.getColor(ctx, id)

    @ColorInt
    inline fun color(@AttrRes attrResId: Int, @StyleRes themeResId: Int): Int =
        color(TypedValue().also {
            ContextThemeWrapper(ctx, themeResId).theme.resolveAttribute(
                attrResId,
                it,
                true
            )
        }.resourceId)

    @ColorRes
    inline fun colorRes(@AttrRes attrResId: Int, @StyleRes themeResId: Int): Int =
        TypedValue().also {
            ContextThemeWrapper(ctx, themeResId).theme.resolveAttribute(
                attrResId,
                it,
                true
            )
        }.resourceId

    @Throws(Resources.NotFoundException::class)
    inline fun colorStateList(@ColorRes id: Int): ColorStateList? =
        ContextCompat.getColorStateList(ctx, id)

    @Throws(Resources.NotFoundException::class)
    inline fun colorStateList(@ColorRes id: Int, @StyleRes themeResId: Int): ColorStateList? =
        ResourcesCompat.getColorStateList(
            ctx.resources,
            id,
            ContextThemeWrapper(ctx, themeResId).theme
        )

    inline fun colorStateListFromAttr(
        @AttrRes attrResId: Int,
        @StyleRes themeResId: Int
    ): ColorStateList =
        ColorStateList.valueOf(color(attrResId, themeResId))

    @Throws(Resources.NotFoundException::class)
    inline fun font(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(ctx, id)

    @Throws(Resources.NotFoundException::class)
    inline fun loadAnimation(@AnimRes id: Int): Animation = AnimationUtils.loadAnimation(ctx, id)

    inline fun resolveAttribute(
        @AttrRes id: Int,
        outValue: TypedValue,
        resolveRefs: Boolean
    ): Boolean =
        ctx.theme.resolveAttribute(id, outValue, resolveRefs)

    inline fun resolveAttribute(
        @AttrRes id: Int,
        @StyleRes themeResId: Int,
        outValue: TypedValue,
        resolveRefs: Boolean
    ): Boolean =
        ContextThemeWrapper(ctx, themeResId).theme.resolveAttribute(id, outValue, resolveRefs)

    @Throws(Resources.NotFoundException::class)
    inline fun value(@DimenRes id: Int, resolveRefs: Boolean): TypedValue = TypedValue().apply {
        ctx.resources.getValue(id, this, resolveRefs)
    }

    inline fun identifier(name: String, defType: String, defPackage: String): Int =
        ctx.resources.getIdentifier(name, defType, defPackage)
}

//Helper extensions
fun View.resourcesProvider() = ResourcesProvider(context)

fun View.resourcesProvider(@StyleRes themeResId: Int) = ResourcesProvider(context, themeResId)

fun Activity.resourcesProvider() = ResourcesProvider(this)

fun Activity.resourcesProvider(@StyleRes themeResId: Int) = ResourcesProvider(this, themeResId)

@Throws(IllegalStateException::class)
fun Fragment.resourcesProvider(): ResourcesProvider = ResourcesProvider(requireContext())

@Throws(IllegalStateException::class)
fun Fragment.resourcesProvider(@StyleRes themeResId: Int) =
    ResourcesProvider(requireContext(), themeResId)