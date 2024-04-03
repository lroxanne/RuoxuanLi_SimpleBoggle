package com.example.ruoxuanliboggle
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), Fragment1.GameplayActionsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.upper_fragment_container, Fragment1().apply {
                    gameplayActionsListener = this@MainActivity
                })
                .commit()
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.lower_fragment_container, Fragment2())
                .commit()
        }

    }
    override fun onScoreUpdated(newScore: Int) {
        val fragment2 = supportFragmentManager.findFragmentById(R.id.lower_fragment_container) as? Fragment2
        fragment2?.updateScore(newScore)
    }
}