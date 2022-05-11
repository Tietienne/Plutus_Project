package com.example.plutus_project

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    private lateinit var todoDao: TodoDatabaseDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context =               InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun deleteDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() = runBlocking {
        val todoItem = TodoItem(itemId = 1, itemName = "Dummy Item", isDone = false)
        todoDao.insert(todoItem)
        val oneItem = todoDao.getById(1)
        assertEquals(oneItem?.itemId, 1)
    }
}