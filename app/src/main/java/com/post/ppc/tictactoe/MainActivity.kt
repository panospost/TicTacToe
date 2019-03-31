package com.post.ppc.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.random.Random


class MainActivity : AppCompatActivity(),View.OnClickListener {


    private val buttons = Array<Array<Button?>>(3) { arrayOfNulls(3) }
    private var MainBoard = arrayOf("","","","","","","","","")
     var game = Game

    //3 * i+j convert 2d to 1 d index

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maint)
        setUpUI()
    }
    /*
    * sets up all the buttons with the click listener
    * */
   fun setUpUI() {
       for ((i, row) in buttons.withIndex()) {
           for ((j, column) in row.withIndex()) {
               // create the id of the button
               val buttonId = "button_$i$j"
               // find the id of the button in layout with  the string created from indexes
               val resId = resources.getIdentifier(buttonId,"id",packageName)
               val button:Button? = findViewById(resId)
               buttons[i][j] = button
               buttons[i][j]?.setOnClickListener(this)
           }
       }
   }

    override fun onClick(tile: View?) {
        game.minimaxcalls = 0
        //increment game Rounds
        game.gameRounds++
        //do nothing when user clicks a tile thats already click
        if ((tile as Button).text.toString() != "") {
            return
        }
        //set tile with the choice of the player
        tile.text = game.playerHuman
        val tileclicked =tile.getTag().toString().toInt()
        game.MainBoard[tileclicked] = game.playerHuman
        game.updateAvailableSpots()

        if(tileclicked == 4 && game.gameRounds==1){
            //player played in the middle of the board, the ai has 4 options to play to prevent
            // the player to create the 3 straight
            val random = Random(4)
                val availabeOptions = arrayOf(intArrayOf(0,0),intArrayOf(0,2),intArrayOf(2,0), intArrayOf(2,2))
                val randomChoice = random.nextInt(3)
                buttons[availabeOptions[randomChoice][0]][availabeOptions[randomChoice][1]]?.text = game.aiPlayer
                game.MainBoard[3*availabeOptions[randomChoice][0]+availabeOptions[randomChoice][1]] = game.aiPlayer

        }else {
            // tale the position of the ai
            //the turn of the ai
            aiTurnAsync()
        }

        Handler().postDelayed({
            checkForWin()
        }, 500)

    }

    private fun aiTurnAsync(){

        doAsync {
            val aiMove = game.turnOfAI()
            if(aiMove[0] != -1){
                uiThread{
                    buttons[aiMove[0]][aiMove[1]]?.text = game.aiPlayer
                }

            }
        }
    }
   fun checkForWin() {
       val result = game.checkIfGameEnded()
       when(result){
           game.playerHuman->{
               Toast.makeText(this,"You Win",Toast.LENGTH_SHORT).show()
               resetGameBoard()
           }
           game.aiPlayer->{
               Toast.makeText(this,"You Lost",Toast.LENGTH_SHORT).show()
               resetGameBoard()
           }
           game.draw ->{
               Toast.makeText(this,"Draw",Toast.LENGTH_SHORT).show()
               resetGameBoard()
           }
       }
   }
    /*
    * resets buttons text and game object properties
    * */
    private fun resetGameBoard() {
        for ((i, row) in buttons.withIndex()) {
            for ((j, column) in row.withIndex()) {
                buttons[i][j]?.text = ""
            }
        }
        game.resetBoard()
    }



}
