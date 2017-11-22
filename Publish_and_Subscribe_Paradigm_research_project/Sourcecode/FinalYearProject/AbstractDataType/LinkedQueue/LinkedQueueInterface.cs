using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FinalYearProject.AbstractDataType.LinkedQueue
{
    public interface LinkedQueueInterface<T>
    {
        void enqueue(T newEntry);
        T dequeue();
        T getFront();
        Boolean isEmpty();
        void clear();
    }
}
