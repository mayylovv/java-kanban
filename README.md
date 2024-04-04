# java-kanban

Kanban is a task management system based on workflow visualization. She helps teams work more efficiently, focus on priorities, and continually improve their work.

Kanban operates with three types of tasks:
- Task: A basic unit of work.
- Epic: A large, complex task that can be broken down into smaller tasks.
- Subtask: A smaller task that is part of an epic.

Task Stages:
- NEW: The task has been created but not yet started.
- IN_PROGRESS: The task is being worked on.
- DONE: The task is complete.

Each task type has the following methods:
- Get all tasks: Retrieves a list of all tasks of the specified type.
- Delete all tasks: Deletes all tasks of the specified type.
- Get by ID: Retrieves a task by its ID.
- Create: Creates a new task.
- Update: Updates an existing task.
- Delete by ID: Deletes a task by its ID.

Tech Stack 
Java Core, JUnit 5, Gson, HttpServer, KVServer
