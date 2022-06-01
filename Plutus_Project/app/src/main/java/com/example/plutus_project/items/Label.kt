package com.example.plutus_project.items

import android.util.Log
import com.example.plutus_project.display.AutoCompleteEntity
import java.util.*

class Label(var id : Int, var text : String) : AutoCompleteEntity {
    override fun filter(query: String): Boolean {
        return text.startsWith(query)
    }

    override fun equals(other: Any?)
            = (other is Label)
            && id == other.id
            && text == other.text

    override fun hashCode(): Int {
        return Objects.hash(id, text)
    }
}