using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace FinalYearProject.AbstractDataType.Signal
{
    [Serializable]
    public class SignalADTException : System.Exception
    {
        public SignalADTException()
        : base() { }
    
        public SignalADTException(string message)
            : base(message) { }
    
        public SignalADTException(string format, params object[] args)
            : base(string.Format(format, args)) { }
    
        public SignalADTException(string message, Exception innerException)
            : base(message, innerException) { }
    
        public SignalADTException(string format, Exception innerException, params object[] args)
            : base(string.Format(format, args), innerException) { }
    
        protected SignalADTException(SerializationInfo info, StreamingContext context)
            : base(info, context) { }

    }
}