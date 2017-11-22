using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Optimization;
using System.Web.Routing;
using FinalYearProject.DataAccessLayer;
using System.Threading;
using FinalYearProject.Class.SubscribeModule;
using System.Runtime.Remoting.Messaging;
using FinalYearProject.Models.StockAPI;


namespace FinalYearProject
{
    public class MvcApplication : System.Web.HttpApplication
    {
        private delegate void checkingValueDelegate();

        protected void Application_Start()
        {
            AreaRegistration.RegisterAllAreas();
            FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters);
            RouteConfig.RegisterRoutes(RouteTable.Routes);
            BundleConfig.RegisterBundles(BundleTable.Bundles);

            System.Diagnostics.Debug.WriteLine("test1");
            checkingValueDelegate tmpAsynchronousMethod = new checkingValueDelegate(checkingValue);
            IAsyncResult tag = tmpAsynchronousMethod.BeginInvoke(null, null);
        }


        private void checkingValue()
        {
            while (YahooCheckerFlag.isSwitchON)
            {
                new yahooModel();
                Thread.Sleep(1800000);
            }
        }


        protected void Application_End()
        {
            YahooCheckerFlag.isSwitchON = false;
            DatabaseDAL.getInstance().Server.Disconnect();
        }      

    }
}
