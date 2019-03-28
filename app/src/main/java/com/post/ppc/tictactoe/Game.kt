package com.post.ppc.tictactoe

import android.util.Log
import java.lang.Exception


object Game {
    var gameRounds = 0
    var minimaxcalls = 0
    var playerHuman = "X"
    var aiPlayer = "O"
    var draw = "draw"
    var MainBoard = arrayOf("", "", "", "", "", "", "", "", "")
    var availableSpots = emptyIndexes(MainBoard)


    fun resetBoard() {
        gameRounds = 0
        MainBoard = arrayOf("", "", "", "", "", "", "", "", "")
        updateAvailableSpots()
        minimaxcalls = 0
    }

    fun turnOfAI(): List<Int> {
        var bestMove = minimax(MainBoard, aiPlayer)
        if(bestMove.Index !=-1 && bestMove.Index !=1000){
            val x = bestMove.Index / 3
            val y = bestMove.Index % 3
            // buttons[x][y]?.text = aiPlayer
            // return the chosen button of ai
            MainBoard[bestMove.Index] = aiPlayer
            return   listOf(x, y)
        }else{
            return listOf(-1,-1)
        }


    }

    //winning combinations
    private fun winning(board: Array<String>, player: String): Boolean {
        return (board[0] == player && board[1] == player && board[2] == player) ||
                (board[3] == player && board[4] == player && board[5] == player) ||
                (board[6] == player && board[7] == player && board[8] == player) ||
                (board[0] == player && board[3] == player && board[6] == player) ||
                (board[1] == player && board[4] == player && board[7] == player) ||
                (board[2] == player && board[5] == player && board[8] == player) ||
                (board[0] == player && board[4] == player && board[8] == player) ||
                (board[2] == player && board[4] == player && board[6] == player)
    }

    private fun emptyIndexes(board: Array<String>): MutableList<Int> {
        val emptyIndex: MutableList<Int> = mutableListOf()
        for (i in board.indices) {
            if (board[i] == "") {
                emptyIndex.add(i)

            }
        }
        return emptyIndex
    }

    private fun minimax(newBoard: Array<String>, player: String): Move {
        //available spots
        val availSpots = emptyIndexes(newBoard)
//        println("empty spots")
//        println(availSpots)

        // checks for the terminal states such as win, lose, and tie and returning a value accordingly
        // and stops the recursion
        when{
            winning(newBoard, playerHuman)->{
                // player won
              //  print("player Won")
                return Move(-1,-10)
            }
            winning(newBoard, aiPlayer)->{
                // ai won
             //   print("ai Won")
                return Move(1000,10)
            }
            availSpots.isEmpty()->{
                // draw
                return Move()
            }
        }

        // an array to collect all the objects
        var moves: MutableList<Move> = mutableListOf()

        // loop through available spots
        for (i in availSpots.indices) {
            //create an object for each and store the index of that spot that was stored as a number in the object's index key
            var move = Move()
            move.Index = availSpots[i]

            // set the empty spot to the current player
            newBoard[availSpots[i]] = player

            //if collect the score resulted from calling minimax on the opponent of the current player

                when (player) {
                    aiPlayer -> {
                        if(minimaxcalls <100){
                            var result = minimax(newBoard, playerHuman)
    //                        println("ai result")
    //                        println(result)
                            move.Score = result.Score
                        }
                    }
                    playerHuman -> {
                        if(minimaxcalls <100){
                            val result = minimax(newBoard, aiPlayer)
                            move.Score = result.Score
                        }

                    }
                }


            //reset the spot to empty
            newBoard[availSpots[i]] = "";
            // push the object to the array
            moves.add(move)
        }
        minimaxcalls++
        // if it is the computer's turn loop over the moves and choose the move with the highest score
        return when (player) {
            aiPlayer -> {
                findNextMove(moves,"best")
            }
            playerHuman -> {
                // else loop over the moves and choose the move with the lowest score
                findNextMove(moves,"worst")
            }
            else -> {
                return Move(0,0)
            }
        }
        // return the chosen move (object) from the array to the higher depth

    }

    data class Move(var Index: Int = -1, var Score: Int = -1)

    private fun findNextMove(allMoves: MutableList<Move>, bestOrWorst: String ): Move {
        var bestMove = Move()
        when(bestOrWorst){
           "best"->{
               var scoreToBeat = -1000
               for(i in allMoves.indices){
                        if (allMoves[i].Score > scoreToBeat) {
                            scoreToBeat = allMoves[i].Score
                            bestMove = allMoves[i]

                        }
               }
           }
            "worst"->{
                var scoreToBeat = 1000
                for(i in allMoves.indices){
                    if (allMoves[i].Score < scoreToBeat) {
                        scoreToBeat = allMoves[i].Score
                        bestMove = allMoves[i]
                    }
                }
            }
        }
        return bestMove
    }

    fun checkIfGameEnded(): String{
        //available spots
        val availSpots = emptyIndexes(MainBoard)
        // checks for the terminal states such as win, lose, and tie and returning a value accordingly
        when{
            winning(MainBoard, playerHuman)->{
                // player won
                return playerHuman
            }
            winning(MainBoard, aiPlayer)->{
                // ai won
                return aiPlayer
            }
            availSpots.isEmpty()->{
                // draw
                return draw
            }
            else->{
                return "onGoing"
            }
        }
    }

    fun updateAvailableSpots() {
        availableSpots = emptyIndexes(MainBoard)
    }
}
