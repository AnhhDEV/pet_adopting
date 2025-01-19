package com.tanh.petadopt.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val OutlinedMap: ImageVector
    get() {
        if (_Map1 != null) {
            return _Map1!!
        }
        _Map1 = ImageVector.Builder(
            name = "Map",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.5f, 5.77705f)
                verticalLineTo(12.0978f)
                lineTo(5.5f, 10.2228f)
                verticalLineTo(3.90205f)
                lineTo(2.5f, 5.77705f)
                close()
                moveTo(6.5f, 3.90205f)
                verticalLineTo(10.2228f)
                lineTo(9.5f, 12.0978f)
                verticalLineTo(5.77705f)
                lineTo(6.5f, 3.90205f)
                close()
                moveTo(6f, 11.0896f)
                lineTo(2.265f, 13.4239f)
                lineTo(1.5f, 12.9999f)
                verticalLineTo(5.49993f)
                lineTo(1.735f, 5.07593f)
                lineTo(5.735f, 2.57593f)
                horizontalLineTo(6.265f)
                lineTo(10f, 4.9103f)
                lineTo(13.735f, 2.57593f)
                lineTo(14.5f, 2.99993f)
                verticalLineTo(10.4999f)
                lineTo(14.265f, 10.9239f)
                lineTo(10.265f, 13.4239f)
                horizontalLineTo(9.735f)
                lineTo(6f, 11.0896f)
                close()
                moveTo(10.5f, 5.77705f)
                verticalLineTo(12.0978f)
                lineTo(13.5f, 10.2228f)
                verticalLineTo(3.90205f)
                lineTo(10.5f, 5.77705f)
                close()
            }
        }.build()
        return _Map1!!
    }

private var _Map1: ImageVector? = null

public val FilledMap: ImageVector
    get() {
        if (_Map != null) {
            return _Map!!
        }
        _Map = ImageVector.Builder(
            name = "Map",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.5f, 5.77705f)
                verticalLineTo(12.0978f)
                lineTo(5.5f, 10.2228f)
                verticalLineTo(3.90205f)
                lineTo(2.5f, 5.77705f)
                close()
                moveTo(6.5f, 3.90205f)
                verticalLineTo(10.2228f)
                lineTo(9.5f, 12.0978f)
                verticalLineTo(5.77705f)
                lineTo(6.5f, 3.90205f)
                close()
                moveTo(6f, 11.0896f)
                lineTo(2.265f, 13.4239f)
                lineTo(1.5f, 12.9999f)
                verticalLineTo(5.49993f)
                lineTo(1.735f, 5.07593f)
                lineTo(5.735f, 2.57593f)
                horizontalLineTo(6.265f)
                lineTo(10f, 4.9103f)
                lineTo(13.735f, 2.57593f)
                lineTo(14.5f, 2.99993f)
                verticalLineTo(10.4999f)
                lineTo(14.265f, 10.9239f)
                lineTo(10.265f, 13.4239f)
                horizontalLineTo(9.735f)
                lineTo(6f, 11.0896f)
                close()
                moveTo(10.5f, 5.77705f)
                verticalLineTo(12.0978f)
                lineTo(13.5f, 10.2228f)
                verticalLineTo(3.90205f)
                lineTo(10.5f, 5.77705f)
                close()
            }
        }.build()
        return _Map!!
    }


private var _Map: ImageVector? = null
