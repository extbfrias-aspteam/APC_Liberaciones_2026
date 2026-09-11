using Microsoft.AspNetCore.DataProtection.KeyManagement;
using System.Security.Cryptography;
using System.Text;
using static System.Net.Mime.MediaTypeNames;

namespace iRisCore.Tools
{
    public class DecryptAndEncrypt
    {


        public string Encrypt(string plainText, string key, string IV)
        {
            byte[] plaintextbytes = System.Text.ASCIIEncoding.ASCII.GetBytes(plainText);
            using (Aes aes = Aes.Create())
            {
                aes.BlockSize = 128;
                aes.KeySize = 256;
                aes.Key = Convert.FromBase64String(key);
                aes.IV = Convert.FromBase64String(IV);
                aes.Padding = PaddingMode.PKCS7;
                aes.Mode = CipherMode.CBC;
                ICryptoTransform crypto = aes.CreateEncryptor(aes.Key,aes.IV);
                byte[] encrypted = crypto.TransformFinalBlock(plaintextbytes, 0, plaintextbytes.Length);
                crypto.Dispose();
                return Convert.ToBase64String(encrypted);
            }
        }
    }
}
