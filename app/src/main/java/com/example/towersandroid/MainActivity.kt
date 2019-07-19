package com.example.towersandroid

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.graphics.BitmapFactory
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import java.lang.Exception
import android.view.ViewGroup




class MainActivity : AppCompatActivity() {

    var _disks = listOf<DiskClass>()
    var _DiskCount = 3
    var pageWidth = 0
    var pageHeight = 0
    var mAnim = AnimatorSet()
    var anims = listOf<Animator>()
    var moves = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.relview)
        setupLayout()
       addListenerOnButton()
        val disks = findViewById<EditText>(R.id.txtDiskCount)
        disks.setText("3")
        disks.isEnabled = false

        findViewById<ImageView>(R.id.btnRWD).visibility = View.GONE
    }

    /**
     * Click events for buttons
     */
    fun addListenerOnButton(){
        //Solve Button
        findViewById<ImageView>(R.id.imageView3).setOnClickListener {
            it.visibility = View.GONE
            findViewById<ImageView>(R.id.btnRWD).visibility = View.VISIBLE
            moves = listOf() //Clear List of moves
            anims = listOf()//Clear the animations
            //resetDisks()
            move(_DiskCount, 'A', 'C', 'B')
            mAnim.playSequentially(anims.toMutableList())
            mAnim.start()

            //Display Move Set
           /* var mess = moves.joinToString { it -> "${it} \n\r" }
            mess = mess.replace(",","")
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("Move List")
            dialog.setMessage(mess)
            dialog.setPositiveButton("Dismiss") { dialog, which ->

            }
            dialog.show()*/
        }
        //DiskSelector
        findViewById<Button>(R.id.btnDown).setOnClickListener {
            //Count down to 3
            val txtDiskCount = findViewById<EditText>(R.id.txtDiskCount)
            var diskCount = txtDiskCount.text.toString().toInt()
            if(diskCount>3){
                diskCount--
                txtDiskCount.setText(diskCount.toString())
                _DiskCount = diskCount
                resetDisks()
            }

        }
        findViewById<Button>(R.id.btnUp).setOnClickListener {
            //Count Up to 8
            val txtDiskCount = findViewById<EditText>(R.id.txtDiskCount)
            var diskCount = txtDiskCount.text.toString().toInt()
            if(diskCount<8){
                diskCount++
                txtDiskCount.setText(diskCount.toString())
                _DiskCount = diskCount
                resetDisks()
            }
        }
        findViewById<ImageView>(R.id.btnRWD).setOnClickListener {
            if(mAnim.isRunning){
                mAnim.end()
            }
            resetDisks()

            it.visibility = View.GONE
            findViewById<ImageView>(R.id.imageView3).visibility = View.VISIBLE
        }
    }

    /**
     * Clear all disks in view
     */
    fun clearDisks(){
        var main = findViewById<RelativeLayout>(R.id.MainView)
        for(disk in _disks){
            val myView = disk.box
            val parent = myView.parent as ViewGroup
            parent.removeView(myView)
        }
    }

    /**
     * Reset all disks in layout
     */
    fun resetDisks(){
        clearDisks()
        _disks = listOf()
        setDisks(_DiskCount)
    }
    /**
     * Setup layout for Towers
     */
    private fun setupLayout() {
        val main = findViewById<RelativeLayout>(R.id.MainView)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        pageWidth = displayMetrics.widthPixels
        pageHeight = displayMetrics.heightPixels
        //Set Floor
        main.addView(setFloor())
        //Set Peg Left
        main.addView(setPeg('A'))
        //Set Peg middle
        main.addView(setPeg('B'))
        //Set Peg right
        main.addView(setPeg('C'))

        setDisks(_DiskCount)
    }

    /**
     * Set Floor for Towers
     */
    private fun setFloor(): View {
        var floor = ImageView(this)
        var bottom_layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pageHeight / 10)
        bottom_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        //floor.setBackgroundColor(Color.GREEN)
        floor.tag = "floor"
        floor.layoutParams = bottom_layoutParams

        val bImage = BitmapFactory.decodeResource(this.resources, R.mipmap.basetexture)
        floor.setImageBitmap(bImage)
        floor.scaleType = ImageView.ScaleType.CENTER_CROP
        return floor
    }

    /**
     * Set Peg for Towers
     * @param pos Char Tower Identifier "ABC"
     */
    private fun setPeg(pos: Char): View {
        var peg = ImageView(this)
        var peg_layoutParams = RelativeLayout.LayoutParams(50, (pageHeight / 10) * 6)
        peg_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        peg_layoutParams.leftMargin = (pageWidth / 4 * getPos(pos)) - 25
        peg_layoutParams.bottomMargin = pageHeight / 10
        peg.layoutParams = peg_layoutParams
        peg.tag = pos.toString()
        val bImage = BitmapFactory.decodeResource(this.resources, R.mipmap.pillartexture)
        peg.setImageBitmap(bImage)
        peg.scaleType = ImageView.ScaleType.CENTER_CROP
        return peg
    }

    /**
     * Create Disks based on required amount
     */
    private fun setDisks(numberOfDisks: Int) {
        val ShrinkSize = 40
        val DiskHeight = 60
        var ii = 1
        for (i in numberOfDisks downTo 1) {
            var disk = ImageView(this)
            val bImage = BitmapFactory.decodeResource(this.resources, R.mipmap.goldrings)
            val diskWidth = (pageWidth / 4) - (ShrinkSize * ii)
            disk.setImageBitmap(bImage)
            disk.tag ="DISK"
            disk.scaleType = ImageView.ScaleType.CENTER_CROP
            var diskParams = RelativeLayout.LayoutParams(diskWidth, DiskHeight)
            //diskParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            val leftPos = getLeftPos( diskWidth, 'A')//((width/4) *getPos('A')) -(diskWidth /2))
            //var floor = findViewById<RelativeLayout>(R.id.MainView).findViewWithTag<ImageView>("floor")
            var topPos = (pageHeight - (pageHeight / 10))
            topPos -= (DiskHeight * ii)

            disk.y = topPos.toFloat()
            disk.x = leftPos.toFloat()
           disk.layoutParams = diskParams

            _disks += DiskClass(diskNo = i, box = disk, peg = 'A',animX = disk.x,animY = disk.y)
            ii++
        }
        addDisksToView()
    }

    /**
     * Add disks to main view
     */
    fun addDisksToView() {

        val main = findViewById<RelativeLayout>(R.id.MainView)
        for (disk: DiskClass in _disks) {
            main.addView(disk.box)
        }
    }

    /**
     * Get left position for desired Disk
     * @param diskWidth Width of disk
     * @param peg Peg that the Disk is assigned to
     * @return Left location in pixels for desired Disk
     */
    fun getLeftPos(diskWidth: Int, peg: Char): Int {
        return (((pageWidth / 4) * getPos(peg)) - (diskWidth / 2))
    }

    /**
     * Get Peg number from char
     * @param peg Char of Peg "ABC"
     * @return Int Peg number 1/2/3
     */
    private fun getPos(peg: Char): Int {
        return when (peg) {
            'A' -> 1
            'B' -> 2
            'C' -> 3
            else -> 0
        }
    }

    /**
     * Recursive call to solve Tower
     * @param n Disk number to move
     * @param startPeg Peg to move from
     * @param endPeg Peg to move to
     * @param tempPeg Auxiliary peg / intermediate Peg that can be used
     */
    private fun move(n: Int, startPeg: Char, endPeg: Char, tempPeg: Char) {
        if (n > 0) {
            move(n - 1, startPeg, tempPeg, endPeg)
            val _move = "Move disk $n from $startPeg to $endPeg"
            println(_move)
            moves += _move

            //Animate
            val disk = _disks.find { x -> x.diskNo == n }
            val box = disk!!.box
            disk!!.peg = endPeg
            //TODO Animate Disks to show user Moves

             val upperMargin = 50f

            //Set Anim Locations
            disk.animX = box.x
            disk.animY = box.y
             val animUp = ObjectAnimator.ofFloat(box, View.TRANSLATION_Y,disk.animY,upperMargin)
            disk.animY = upperMargin
             animUp.duration = 1000

             anims += animUp
             if (startPeg < endPeg){
                 val pos = getLeftPos(box.width,endPeg).toFloat()
                 val from = getLeftPos(box.width,startPeg).toFloat()
                 val animRight = ObjectAnimator.ofFloat(box, View.TRANSLATION_X,from, pos)
                 disk.animX = pos
                 animRight.duration = 1000
                 anims += animRight
             }else{
                 val pos = getLeftPos(box.width,endPeg).toFloat()
                 val from = getLeftPos(box.width,startPeg).toFloat()
                 val animLeft = ObjectAnimator.ofFloat(box, View.TRANSLATION_X,from,pos )
                 disk.animX = pos
                 animLeft.duration = 1000
                 anims += animLeft
             }
             val pegCount =_disks.filter  { x->x.peg==endPeg }.size
             val pos = (((pageHeight- (pageHeight/10)) - (box.height*(pegCount)))).toFloat()
             val animDown = ObjectAnimator.ofFloat(box, View.TRANSLATION_Y,disk.animY, pos)
            disk.animY = pos
             animDown.duration = 1000

             anims += animDown

            move(n - 1, tempPeg, endPeg, startPeg)
        }
    }
}
