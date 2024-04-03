package com.example.ruoxuanliboggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import java.util.Locale

class Fragment1 : Fragment() {

    private lateinit var lettersGrid: GridLayout
    private lateinit var selectedLettersTextView: TextView
    private var selectedLetters = StringBuilder()
    private var currentScore: Int = 0
    private var dictionaryWords: Set<String> = emptySet()
    private var lastSelectedLetter: Button? = null



    var gameplayActionsListener: GameplayActionsListener? = null

    interface GameplayActionsListener {
        fun onScoreUpdated(score: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment1, container, false)
        lettersGrid = view.findViewById(R.id.lettersGrid)
        selectedLettersTextView = view.findViewById(R.id.selectedLettersTextView)
        loadDictionary(requireContext())
        initializeGrid()
        view.findViewById<Button>(R.id.clearButton).setOnClickListener {
            clearSelection()
        }
        view.findViewById<Button>(R.id.submitButton).setOnClickListener {
            val currentWord = selectedLetters.toString().lowercase(Locale.getDefault())
            calculateScore(currentWord)
            clearSelection()
        }
        return view
    }

    private fun loadDictionary(context: Context) {
        val inputStream = context.assets.open("word.txt")
        dictionaryWords = inputStream.bufferedReader().useLines { lines ->
            lines.map { it.lowercase(Locale.getDefault()) }.toSet()
        }
    }

    private fun calculateScore(word: String) {
        if (!dictionaryWords.contains(word)) {
            Toast.makeText(context, "Word not in dictionary", Toast.LENGTH_SHORT).show()
            currentScore -= 10
        } else {
            val consonants = word.filter { it in "BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz" }
            val vowels = word.filter { it in "AEIOUaeiou" }
            var wordScore = consonants.length + vowels.length * 5
            if (word.any { it in "SZPXQszpxq" }) {
                wordScore *= 2
            }
            currentScore += wordScore
            Toast.makeText(context, "Valid word: +$wordScore points", Toast.LENGTH_SHORT).show()
        }

        // Update the UI or notify the activity of the score change
        gameplayActionsListener?.onScoreUpdated(currentScore)
    }

    private fun initializeGrid() {
        lettersGrid.removeAllViews()
        lettersGrid.columnCount = 4 // set column count for the grid
        lettersGrid.rowCount = 4    // set row count for the grid
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val letter = ('A'..'Z').random().toString()
                val button = Button(context).apply {
                    text = letter
                    tag = Pair(i, j)  // Store the row and column as a tag
                    layoutParams = GridLayout.LayoutParams(
                        GridLayout.spec(i, 1f),
                        GridLayout.spec(j, 1f)
                    ).apply {
                        width = 0
                        height = 0
                        setMargins(5, 5, 5, 5)
                    }
                    setOnClickListener {
                        handleLetterClick(this)
                    }
                }
                lettersGrid.addView(button)
            }
        }



    }
    private fun handleLetterClick(button: Button) {
        val (row, col) = button.tag as Pair<Int, Int>
        val lastPos = lastSelectedLetter?.tag as? Pair<Int, Int>

        // If it's the first letter, or if it's adjacent to the last selected letter, allow selection
        if (lastPos == null || isAdjacent(lastPos, Pair(row, col))) {
            selectedLetters.append(button.text)
            selectedLettersTextView.text = selectedLetters.toString()
            lastSelectedLetter = button
            button.isEnabled = false // Disable the button after selection
        } else {
            Toast.makeText(context, "Please select an adjacent letter", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isAdjacent(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
        val (row1, col1) = pos1
        val (row2, col2) = pos2
        return Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1 && !(row1 == row2 && col1 == col2)
    }



    private fun clearSelection() {
        selectedLetters.clear()
        selectedLettersTextView.text = ""
        lastSelectedLetter = null
        lettersGrid.children.forEach { it.isEnabled = true }
        lettersGrid.removeAllViews() // 或者逐个重置按钮状态
        initializeGrid()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameplayActionsListener) {
            gameplayActionsListener = context
        } else {
            throw RuntimeException("$context must implement GameplayActionsListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        gameplayActionsListener = null
    }
}

