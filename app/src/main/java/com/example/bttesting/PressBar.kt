package com.example.bttesting

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import java.util.*
import kotlin.math.min

/*SISTEMARE DIMENSIONI IN MANIERA EFFICIENTE!!!!!!!!!!!!!!!!!!!! in modo che il calcolo possa essere efficiente
selezionare dei valori minimi e
EFFICIENZA!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */

/* SI POSSONO FISSARE DEI VALORI MINIMI!!!!

 */

/* forse e' meglio aumentare lo spessore della griglia!!!

 */

/*valori da mettere dentro customView

 */

class PressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)  {


    private var lengthBar = 0.0f
    private var heightBar = 0.0f
    private var maxValue = 0
    private var actualValue = 15
    private var minValue = 0
    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    private var lunghUnit = 0.0f
    val random = Random()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    init {
        isClickable = false
        context.withStyledAttributes(attrs, R.styleable.PressBar) {
            //fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            actualValue = getInteger(R.styleable.PressBar_valoreAttuale, 15)
            maxValue = getInteger(R.styleable.PressBar_valoreMassimo, 0)
            minValue = getInteger(R.styleable.PressBar_valoreMinimo,0)
        }
    }

    public fun cambiaValori(valori:MutableList<Int>){
        minValue= valori[2]
        actualValue= valori[1]
        maxValue=valori[0]
    }

    //se cambia la dimensione della finestra disegna un cerchio sulla dimensione minore (lunghezza o larghezza)
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        lengthBar= width.toFloat()
        heightBar=height.toFloat()
        lunghUnit=lengthBar/45
    }

    //calcola F sulla base della lunghezza barra e valore
    private fun PointF.computeXYforValues(min: Float, max: Float):Rect{
        //val lunghUnit = lungBarra/30 //lunghezza unitaria
        //x=lunghUnit*value
        //y= heightBar //si puo' togliere perche' tanto non cambia!!!!!
        //var rect = Rect((min*lunghUnit).toInt(), heightBar.toInt(), 0, ((max+1)*lunghUnit).toInt())
        var minLun = (min*lunghUnit).toInt()
        var maxlun = (max*lunghUnit).toInt()
        var rect = Rect(minLun, (heightBar*0.85).toFloat().toInt(), maxlun, 0)
        return rect
    }


    private fun PointF.computeXYforValueMin(value: Float){
        //val lunghUnit = lungBarra/30 //lunghezza unitaria
        x=lunghUnit*value
        y= heightBar //si puo' togliere perche' tanto non cambia!!!!!
    }

    private fun PointF.computeXYforValue(value: Float){
        //val lunghUnit = lungBarra/30 //lunghezza unitaria
        x=lunghUnit*value
        y= heightBar //si puo' togliere perche' tanto non cambia!!!!!
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //migliaia di altri dati da scrivere e valori da vedere
        //canvas.drawRect(5F, 50F, 55F, 0F, paint) //cerchio
        //canvas.drawLine(0F,0F,20F,20F, paint)

        /*val rect = pointPosition.computeXYforValues(minValue.toFloat(), maxValue.toFloat())
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        canvas.drawRect(rect, paint)*/
        drawRect(minValue, maxValue, canvas)

        //reticolo
        //rettangolo esterno
        val outernRect = Rect(0, 0, lengthBar.toInt(), (heightBar*0.85).toInt())
        paint.color= Color.WHITE
        paint.style = Paint.Style.STROKE
        canvas.drawRect(outernRect, paint)

        //rettangolo interno
        for (i in 1..44){
            pointPosition.computeXYforValue(i.toFloat())
            //paint.color=Color.WHITE
            canvas.drawLine(pointPosition.x,0f,pointPosition.x,(heightBar*0.85).toFloat(), paint)
        }

        //linea in fondo
        canvas.drawLine(0f,(heightBar*0.9).toFloat(),lengthBar,(heightBar*0.9).toFloat(), paint)

        //tacchette in fondo e ogni cinque
        for (i in 0..45){
            if(i%5==0){
                pointPosition.computeXYforValue(i.toFloat())
                canvas.drawLine(pointPosition.x,(heightBar*0.9).toFloat(), pointPosition.x, heightBar,paint ) }
        }


        pointPosition.computeXYforValue(value = actualValue.toFloat())
        paint.style = Paint.Style.FILL
        paint.color = Color.BLUE
        canvas.drawRect(pointPosition.x, (heightBar*0.85).toFloat(), pointPosition.x+lunghUnit,0F, paint)
        //canvas.drawRect(0f, 0f, 100f,100f, paint)
    }

    private fun drawRect(minimo:Int, massimo:Int, canvas: Canvas){

        paint.style = Paint.Style.FILL

        //dipingi un rettangolo verde dal maggiore al minore o zero
        if((massimo>15f)&&(15f>=minimo)){
            paint.color = Color.GREEN
            val rectGreen = pointPosition.computeXYforValues(15f, massimo.toFloat())
            canvas.drawRect(rectGreen, paint)

            paint.color=Color.RED
            val rectRed = pointPosition.computeXYforValues(minimo.toFloat(),15f)
            canvas.drawRect(rectRed,paint)
        }
        else{
            if(minimo>15f)
            paint.color = Color.GREEN
            if(massimo<=15){
                val rectGreen = pointPosition.computeXYforValues(minimo.toFloat(), massimo.toFloat())
                canvas.drawRect(rectGreen, paint)
            }
            else{
                val rectRed = pointPosition.computeXYforValues(minimo.toFloat(),massimo.toFloat())
                canvas.drawRect(rectRed, paint)
            }
        }

        //dipingi un rettangolo rosso dal minore al maggiore o zero
    }

}