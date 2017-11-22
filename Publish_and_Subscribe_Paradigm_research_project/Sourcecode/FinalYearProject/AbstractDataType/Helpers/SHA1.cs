using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FinalYearProject.AbstractDataType.Helpers
{
    public class SHA1
    {
        private const string encodeKey = "p&s";
        public static string Encode(string value)
        {
            var hash = System.Security.Cryptography.SHA1.Create();
            var encoder = new System.Text.ASCIIEncoding();
            var combined = encoder.GetBytes(value + encodeKey);

            return BitConverter.ToString(hash.ComputeHash(combined)).ToLower().Replace("-", "");
        }

    }
}