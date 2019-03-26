package com.post.ppc.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.Random


class MainActivity : AppCompatActivity(),View.OnClickListener {


    private val buttons = Array<Array<Button?>>(3) { arrayOfNulls(3) }
    private var MainBoard = arrayOf("","","","","","","","","")
    var game = Game

    //3 * i+j convert 2d to 1 d index

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        //increment game Rounds
        game.gameRounds++
        //what happens when user clicks a tile
        if ((tile as Button).text.toString() != "") {
            return
        }
        //set tile with the choice of the player
        tile.text = game.playerHuman
        val tileclicked =tile.getTag().toString().toInt()
        game.MainBoard[tileclicked] = game.playerHuman
        game.updateAvailableSpots()
        // tale the position of the ai
        //the turn of the ai
        if(game.availableSpots.size >= 1) {
            try{
                val aiMove = game.turnOfAI()

                if(aiMove[0] != -1){
                    buttons[aiMove[0]][aiMove[1]]?.text = game.aiPlayer
                }
            }catch (e: Exception){
                e.printStackTrace()
            }



        }else{
            val result = game.checkIfGameEnded()
            when(result){
                game.playerHuman->{
                    Toast.makeText(this,"You Win",Toast.LENGTH_SHORT).show()
                }
                game.aiPlayer->{
                    Toast.makeText(this,"You Lost",Toast.LENGTH_SHORT).show()
                }
                game.draw ->{
                    Toast.makeText(this,"Draw",Toast.LENGTH_SHORT).show()
                }

            }
        }



    }


    private fun resetBoard() {
        for ((i, row) in buttons.withIndex()) {
            for ((j, column) in row.withIndex()) {
                buttons[i][j]?.text = ""
            }
        }
    }

}
