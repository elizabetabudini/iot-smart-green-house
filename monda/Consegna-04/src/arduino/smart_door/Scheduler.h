#ifndef __SCHEDULER__
#define __SCHEDULER__

#include "Task.h"
#include "Timer.h"

#define MAX_TASKS 10

class Scheduler {
    int basePeriod;
    int nTasks;
    Timer timer;
    Task* taskList[MAX_TASKS];

  public:
    void init(int basePeriod);
    virtual bool addTask(Task* task);
    virtual void schedule();
};

#endif
