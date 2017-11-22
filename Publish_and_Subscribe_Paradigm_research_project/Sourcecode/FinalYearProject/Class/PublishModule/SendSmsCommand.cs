using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.Remoting.Messaging;
using System.Threading;
using System.Web;

namespace FinalYearProject.Class.PublishModule
{
    public class SendSmsCommand : Command
    {
        private delegate bool SendSmsDelegate(String pReceiverPhoneNumber, String pMessageBody);

        private String receiverPhoneNumber; 
        private String messageBody;
        private int smsResendCount = 0;

        public SendSmsCommand(String pReceiverPhoneNumber, String pMessageBody)
        {
            checkPhoneNumberFormat(pReceiverPhoneNumber);
            this.receiverPhoneNumber = pReceiverPhoneNumber;
            this.messageBody = pMessageBody;
        }


        public void execute()
        {
            System.Diagnostics.Debug.WriteLine("pass11");
            SendSmsDelegate tmpAsynchronousMethod =
                       new SendSmsDelegate(sendSms);
            IAsyncResult tag =
                tmpAsynchronousMethod.BeginInvoke(this.receiverPhoneNumber, this.messageBody, this.sendSmsCallBack, null);
            System.Diagnostics.Debug.WriteLine("pass10");
        }


        //this method will thrown an exception if wrong format
        private void checkPhoneNumberFormat(String pReceiverPhoneNumber)
        {
            Int64.Parse(pReceiverPhoneNumber);
        }


        private bool sendSms(String ReceiverPhoneNumber, String messageBody)
        {
            //go sms 99 register an account
            //then chenage the username and password accordingly
            String URL = "http://login.sms99.net/websmsapi/ISendSMS.aspx?username=marshes&password=/*your password here*/&message="
            + messageBody + "&mobile=" + ReceiverPhoneNumber + "&type=1";

            WebRequest request = WebRequest.Create(URL);

            WebResponse response = request.GetResponse();


            if (((HttpWebResponse)response).StatusCode == HttpStatusCode.OK)
            {
                Stream dataStream = response.GetResponseStream();
                StreamReader reader = new StreamReader(dataStream);

                char[] returnedCode = new char[4];
                reader.Read(returnedCode, 0, returnedCode.Length);
                reader.Close();
                response.Close();

                string serverResponse = new string(returnedCode);

                if (serverResponse.Equals("1701"))
                {
                    System.Diagnostics.Debug.Write("endSMS");
                    return true;
                }

            }//end if
            System.Diagnostics.Debug.Write("pass8");
            return false;

        }//end sms sending


        private void sendSmsCallBack(IAsyncResult ar)
        {
            AsyncResult result = (AsyncResult)ar;
            SendSmsDelegate tagetAsynchronousMethod = (SendSmsDelegate)result.AsyncDelegate;
            bool isSending = tagetAsynchronousMethod.EndInvoke(ar);

            if (!isSending)
            {
                if (smsResendCount <= 5)
                {
                    Thread.Sleep(60000); // inactive 1 minute
                    smsResendCount++;

                    SendSmsDelegate tmpAsynchronousMethod =
                            new SendSmsDelegate(sendSms);
                    IAsyncResult tag =
                        tmpAsynchronousMethod.BeginInvoke(this.receiverPhoneNumber, this.messageBody, sendSmsCallBack, null);
                }
            }
        }//end email callback method;


    }//end class

}