package com.post.ppc.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),View.OnClickListener {

    var gameRounds = 0
    var playerHuman = "X"
    var aiPlayer =  "O"
    private var aiStrategy = "nothing"
    private var aiGoal = 3
    private val random: Random = Random(4)
    private val buttons = Array<Array<Button?>>(3) { arrayOfNulls(3) }
    private var MainBoard = arrayOf("","","","","","","","","")

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
        gameRounds++
        //what happens when user clicks a tile
        if ((tile as Button).text.toString() != "") {
            return
        }
        //set tile with the choice of the player
        tile.text = playerHuman
        val tileclicked =tile.getTag().toString().toInt()
        MainBoard[tileclicked] = playerHuman
        // tale the position of the ai
        //the turn of the ai
        turnOfAI()
        if(checkIfPlayerWin()){
            resetBoard()
        }else   if(winning(MainBoard,aiPlayer)){
            Toast.makeText(this, "AiWon", Toast.LENGTH_SHORT).show()
            resetBoard()
        }else if(gameRounds == 5){
            gameIsDraw()
            resetBoard()
        }
    }



    private fun checkIfPlayerWin(): Boolean {

        for( i in 0..2){
            var isIt = buttons[i][0]?.text
            Log.i("myTag","what is it" + isIt + " " + (isIt=="X").toString())

                if(buttons[i][0]?.text == buttons[i][1]?.text && buttons[i][0]?.text == buttons[i][2]?.text && buttons[i][0]?.text == playerHuman){
                    return player1Wins()
                }
                if(i==2){
                    // diagonial right
                    if(buttons[0][0]?.text == buttons[i-1][i-1]?.text && buttons[i-1][i-1]?.text == buttons[i][i]?.text && buttons[i][i]?.text == playerHuman){
                        return player1Wins()
                    }
                    // diagonial left
                    if(buttons[i][0]?.text == buttons[i-1][i-1]?.text && buttons[i-1][i-1]?.text == buttons[0][i]?.text && buttons[0][i]?.text == playerHuman){
                        return player1Wins()
                    }
                }
               if(buttons[0][i]?.text == buttons[1][i]?.text && buttons[0][i]?.text == buttons[2][i]?.text && buttons[0][i]?.text == playerHuman){
                   return player1Wins()
               }
        }
       return false

    }

    private fun gameIsDraw() {
        Toast.makeText(this, "Game is Draw", Toast.LENGTH_SHORT).show()
    }

    private fun player1Wins(): Boolean {
        Toast.makeText(this,"Player WON", Toast.LENGTH_LONG).show()
        return true
    }

    private fun resetBoard() {
        for ((i, row) in buttons.withIndex()) {
            for ((j, column) in row.withIndex()) {
                buttons[i][j]?.text = ""
            }
        }
        gameRounds = 0
        MainBoard = arrayOf("","","","","","","","","")
    }

    private fun turnOfAI() {
        if(gameRounds== 1) {
            if(buttons[1][1]?.text == playerHuman){
                MainBoard[4] = playerHuman
                aiStrategy = "defensive"
            }else{
                aiStrategy = "offensive"
            }

            if(aiStrategy == "defensive") {
                //player played in the middle of the board, the ai has 4 options to play to prevent
                // the player to create the 3 straight
                val availabeOptions = arrayOf(intArrayOf(0,0),intArrayOf(0,2),intArrayOf(2,0), intArrayOf(2,2))
                val randomChoice = random.nextInt(3)
                buttons[availabeOptions[randomChoice][0]][availabeOptions[randomChoice][1]]?.text = aiPlayer
                MainBoard[3*availabeOptions[randomChoice][0]+availabeOptions[randomChoice][1]] = aiPlayer
            }else{
                buttons[1][1]?.text = aiPlayer
            }

         }else{
            val bestMove = minimax(MainBoard,aiPlayer )
            Log.i("Best move", bestMove.Index.toString())
            if(bestMove.Index != -1 && bestMove.Index != 1000) {
                val x = bestMove.Index/3
                val y = bestMove.Index%3
                buttons[x][y]?.text = aiPlayer
                MainBoard[bestMove.Index] = aiPlayer
            }else{
                Log.i("rounds", gameRounds.toString())
            }
        }
    }
    //winning combinations
    private fun winning(board:Array<String>, player: String): Boolean{
        return (board[0] == player && board[1] == player && board[2] == player) ||
                (board[3] == player && board[4] == player && board[5] == player) ||
                (board[6] == player && board[7] == player && board[8] == player) ||
                (board[0] == player && board[3] == player && board[6] == player) ||
                (board[1] == player && board[4] == player && board[7] == player) ||
                (board[2] == player && board[5] == player && board[8] == player) ||
                (board[0] == player && board[4] == player && board[8] == player) ||
                (board[2] == player && board[4] == player && board[6] == player)
    }

    private fun emptyIndexes(board:Array<String>): MutableList<Int> {
        var emptyIndex:MutableList<Int> = mutableListOf()
        for (i in board.indices){
            if(board[i] == ""){
                emptyIndex.add(i)

            }
        }
        return  emptyIndex
    }

    private fun minimax(newBoard: Array<String>, player: String): Move {

        //available spots
        var availSpots = emptyIndexes(newBoard)

        // checks for the terminal states such as win, lose, and tie and returning a value accordingly
        if (winning(newBoard, playerHuman)){
            // player won
            return Move(-1, -10)
            //return {score:-10};
        }
        else if (winning(newBoard, aiPlayer)){
            // ai won
            return Move(1000,10)
        }
        else if (availSpots.isEmpty()){
            // draw

            return Move()
        }

        // an array to collect all the objects
        var moves:MutableList<Move> = mutableListOf()

        // loop through available spots
        for ( i  in availSpots.indices){
            //create an object for each and store the index of that spot that was stored as a number in the object's index key
            val move = Move()
            move.Index = availSpots[i]

            // set the empty spot to the current player
            newBoard[availSpots[i]] = player

            //if collect the score resulted from calling minimax on the opponent of the current player
            if (player == aiPlayer){
                val result = minimax(newBoard, playerHuman);
                move.Score = result.Score;
            }
            else{
                val result = minimax(newBoard, aiPlayer);
                move.Score = result.Score;
            }

            //reset the spot to empty
            newBoard[availSpots[i]] = "";

            // push the object to the array
            moves.add(move)
        }

         // if it is the computer's turn loop over the moves and choose the move with the highest score
        var bestMove = Move()
        if(player === aiPlayer){
            var bestScore = -10000;
            for(i in moves.indices){
                if(moves[i].Score > bestScore){
                    bestScore = moves[i].Score;
                    bestMove = moves[i]
                }
            }
        }else{
           // else loop over the moves and choose the move with the lowest score
            var bestScore = 10000;
            for(i in moves.indices){
                if(moves[i].Score < bestScore){
                    bestScore = moves[i].Score;
                    bestMove = moves[i]
                }
            }
        }

       // return the chosen move (object) from the array to the higher depth
        return bestMove
    }

     data class Move(var Index: Int = -1, var Score: Int = -1)
}
