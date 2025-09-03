import { Application } from 'express';
import axios from 'axios';

export default function (app: Application): void {
  app.get('/tasks', async (req, res) => {
    try {
      // Call your task API
      const response = await axios.get('http://localhost:4000/api/tasks');
      const tasks = response.data;
      
      // Also get statistics for dashboard info
      const statsResponse = await axios.get('http://localhost:4000/api/tasks/stats');
      const stats = statsResponse.data;
      
      res.render('tasks', { 
        tasks: tasks,
        stats: stats
      });
    } catch (error) {
      console.error('Error fetching tasks:', error);
      res.render('tasks', { 
        tasks: [],
        stats: { todoCount: 0, inProgressCount: 0, completedCount: 0 },
        error: 'Could not load tasks'
      });
    }
  });

   app.get('/tasks/create', async (req, res) => {
    res.render('create-task');
  });

  app.post('/tasks/create', async (req, res) => {
    try {
      const taskData = {
        title: req.body.title,
        description: req.body.description,
        status: req.body.status || 'TODO',
        dueDateTime: req.body.dueDateTime
      };

      await axios.post('http://localhost:4000/api/tasks', taskData);
      res.redirect('/tasks');
    } catch (error) {
      console.error('Error creating task:', error);
      res.render('create-task', { 
        error: 'Failed to create task. Please try again.',
        formData: req.body 
      });
    }
  });

  // Show edit task form
app.get('/tasks/:id/edit', async (req, res) => {
  try {
    const taskId = req.params.id;
    const response = await axios.get(`http://localhost:4000/api/tasks/${taskId}`);
    const task = response.data;
    
    res.render('edit-task', { task: task });
  } catch (error) {
    console.error('Error fetching task:', error);
    res.redirect('/tasks');
  }
});

// Handle edit form submission
app.put('/tasks/:id/edit', async (req, res) => {
  try {
    const taskId = req.params.id;
    const taskData = {
      title: req.body.title,
      description: req.body.description,
      status: req.body.status,
      dueDateTime: req.body.dueDateTime
    };

    await axios.put(`http://localhost:4000/api/tasks/${taskId}`, taskData);
    res.redirect('/tasks');
  } catch (error) {
    console.error('Error updating task:', error);
    res.render('edit-task', { 
      error: 'Failed to update task. Please try again.',
      task: { id: req.params.id, ...req.body }
    });
  }
});

// Handle delete task
app.post('/tasks/:id/delete', async (req, res) => {
  try {
    const taskId = req.params.id;
    await axios.delete(`http://localhost:4000/api/tasks/${taskId}`);
    res.redirect('/tasks');
  } catch (error) {
    console.error('Error deleting task:', error);
    res.redirect('/tasks');
  }
});
}