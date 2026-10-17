package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.example.ui.canvas.MindMapLayoutEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MindCraft AI", appName)
    }

    @Test
    fun `test auto layout engine arranges nodes`() {
        val root = MindNode(
            id = "root",
            mapId = "map1",
            parentId = null,
            title = "Root Idea",
            category = NodeCategory.GOAL
        )
        val child1 = MindNode(
            id = "c1",
            mapId = "map1",
            parentId = "root",
            title = "Child 1",
            category = NodeCategory.FEATURE
        )
        val child2 = MindNode(
            id = "c2",
            mapId = "map1",
            parentId = "root",
            title = "Child 2",
            category = NodeCategory.RISK
        )

        val layoutResult = MindMapLayoutEngine.computeBalancedLayout(listOf(root, child1, child2))
        assertEquals(3, layoutResult.size)
        val layoutRoot = layoutResult.first { it.id == "root" }
        assertEquals(0f, layoutRoot.xPos, 0.01f)
        assertEquals(0f, layoutRoot.yPos, 0.01f)
    }
}
