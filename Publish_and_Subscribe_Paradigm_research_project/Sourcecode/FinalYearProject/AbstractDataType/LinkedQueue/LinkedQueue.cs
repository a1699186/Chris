using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FinalYearProject.AbstractDataType.LinkedQueue
{
    public class LinkedQueue<T> : LinkedQueueInterface<T>
    {
        //inner class
        private class Node
        {
            public T data { get; set; }
            public Node next { get; set; }

            public Node(T storedData)
            {
                this.data = storedData;
                next = null;
            }
        } 


        private Node firstQueue = null;
        private Node lastQueue = null;

        public void enqueue(T newEntry)
        {
            Node entryNode = new Node(newEntry);

            if (isEmpty())
            {
                this.firstQueue = entryNode;
            }
            else
            {
                this.lastQueue.next = entryNode;
            }

            this.lastQueue = entryNode;
        }

        public T dequeue()
        {
            T returnValue = default(T);

            if (!isEmpty())
            {
                returnValue = firstQueue.data;
                firstQueue = firstQueue.next;


                if (firstQueue == null)
                {
                    lastQueue = null;
                }
            }

            return returnValue;
        }

        public T getFront()
        {
            if (isEmpty())
            {
                return default(T);
            }

            return firstQueue.data;
        }


        public Boolean isEmpty()
        {
            return firstQueue == null;
        }

        public void clear()
        {
            firstQueue = null;
            lastQueue = null;
        }

    }
}