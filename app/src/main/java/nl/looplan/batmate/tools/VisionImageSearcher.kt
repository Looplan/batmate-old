package nl.looplan.batmate.tools

import android.graphics.Rect
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.*

object VisionImageSearcher {

    class NoMatchingTextBlocksFound : Exception("No matching text blocks were found")

    suspend fun searchForPaperTitle(text : FirebaseVisionText, image : FirebaseVisionImage) : String {
        return withContext(Dispatchers.IO) {
            val blocksInSearchRectangle = getBlocksInSearchRectangle(text, image)

            var title = String()
            try {
                title = getTitleFromBlocksInSearchRectangle(blocksInSearchRectangle)
            } catch(exception : NoMatchingTextBlocksFound) {
                throw exception
            }

            title
        }
    }

    private suspend fun getBlocksInSearchRectangle(text : FirebaseVisionText, image : FirebaseVisionImage) : MutableList<FirebaseVisionText.TextBlock> {
        return withContext(Dispatchers.IO) {

            // Get the bitmap.
            val bitmap = image.bitmap

            // Create rectangle which defines the search area.
            val searchRectangle : Rect = Rect().apply {
                top = 0
                left = 0
                right = bitmap.width
                bottom = bitmap.height / 2
            }

            // Create a list to store block which are in the search rectangle.
            val blocksInSearchRectangle : MutableList<FirebaseVisionText.TextBlock> = mutableListOf()

            // Iterate through each text block.
            for(block in text.textBlocks) {
                // Check if the search rectangle contains the rectangle of the text block.
                val contains = searchRectangle.contains(block.boundingBox!!)
                if(contains) {
                    // Add the block to the list of blocks in the search rectangle.
                    blocksInSearchRectangle.add(block)
                }
            }
            blocksInSearchRectangle
        }
    }

    private fun getTitleFromBlocksInSearchRectangle(blocksInSearchRectangle : MutableList<FirebaseVisionText.TextBlock>) : String {
        // Check how many blocks are in the search rectangle.
        when {
            blocksInSearchRectangle.isEmpty() -> {
                throw NoMatchingTextBlocksFound()
            }
            blocksInSearchRectangle.size == 1 -> {
                return blocksInSearchRectangle.single().text
            }
            blocksInSearchRectangle.size > 1 -> {

                // Sort list to get the right text block.
                val sortedList =
                    blocksInSearchRectangle.sortedWith(compareBy<FirebaseVisionText.TextBlock> { it.boundingBox?.left }.thenByDescending { it.boundingBox?.top })

                // Take the block that is most top and left.
                val block = sortedList.first()

                return block.text
            }
        }
        return String()
    }
}