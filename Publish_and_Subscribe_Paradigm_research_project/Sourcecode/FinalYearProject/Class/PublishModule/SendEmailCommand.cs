using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Runtime.Remoting.Messaging;
using System.Web;
using System.Threading;

namespace FinalYearProject.Class.PublishModule
{
    public class SendEmailCommand : Command
    {
        private delegate bool SendEmailDelegate(String pReceiverEmail, String pHtmlBody);

        private String receiverEmail;
        private String htmlBody;
        private int messageResendCount = 0; 

        public SendEmailCommand(String pReceiverEMail, String pHtmlBody)
        {
            this.receiverEmail = pReceiverEMail;
            this.htmlBody = pHtmlBody;
        }

        public void execute()
        {
            SendEmailDelegate tmpAsynchronousMethod =
                       new SendEmailDelegate(sendEmail);
            IAsyncResult tag =
                tmpAsynchronousMethod.BeginInvoke(this.receiverEmail,this.htmlBody, this.sendEmailCallBack, null);
        }


        private bool sendEmail(String receiverEMail, String htmlBody)
        {
            try
            {
                MailMessage message = new MailMessage();
                message.To.Add(receiverEMail);
                message.From = new MailAddress("Replace with your Gmail account here", "Final year project");
                message.Subject = "Final Year Project";
                message.IsBodyHtml = true;

                message.Body = htmlBody;

                SmtpClient smtpClient = new SmtpClient("smtp.gmail.com");
                smtpClient.EnableSsl = true;
                smtpClient.Port = 587;

                //provide your email information here
                smtpClient.Credentials = new System.Net.NetworkCredential("Replace with your Gmail account here", "Replace with your Gmail's password here.");

                smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
                smtpClient.Timeout = 30000;
                smtpClient.Send(message);
                System.Diagnostics.Debug.Write("endEmail");
                return true;
            }
            catch (Exception)
            {
                System.Diagnostics.Debug.Write("pass 7");
                return false;
            }
        }//end mail sending


        private void sendEmailCallBack(IAsyncResult ar)
        {
            AsyncResult result = (AsyncResult)ar;
            SendEmailDelegate tagetAsynchronousMethod = (SendEmailDelegate)result.AsyncDelegate;
            bool isSending = tagetAsynchronousMethod.EndInvoke(ar);

            if (!isSending)
            {
                if (messageResendCount <= 5)
                {
                    Thread.Sleep(60000); // inactive 1 minute
                    messageResendCount++;

                    SendEmailDelegate tmpAsynchronousMethod =
                            new SendEmailDelegate(sendEmail);
                    IAsyncResult tag =
                        tmpAsynchronousMethod.BeginInvoke(this.receiverEmail, this.htmlBody, sendEmailCallBack, null);
                }
            }
        }//end email callback method;


    } // end class

}