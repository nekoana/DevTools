use jni::JNIEnv;
use jni::objects::JObject;

extern "Rust"{
  fn api_version() -> String;

    /// 签到
    ///*  `number` 工号
    ///* `token` 令牌
    ///* `bmp_path` 签到图片路径
    fn api_signin(number: String, token: String, bmp_path: String) -> Result<(), &'static str>;
}



#[no_mangle]
pub extern "system" fn Java_SignInViewModel_version<'a>(env:JNIEnv<'a>,_obj:JObject<'a>) -> JObject<'a> {
    let version = env.new_string("1.0.0").expect("Couldn't create java string!");

   JObject::from(version)
}


#[cfg(test)]
mod test{
    use crate::*;

    #[test]
    fn test_api_version() {
        let version = unsafe { api_version() };
        assert_eq!(version, "V1.0.0");
    }

    #[test]
    fn test_api_signin() {
        let sigin = unsafe { api_signin("".to_string(), "".to_string(), "".to_string()) };
        assert_eq!(sigin, Err("工号不能为空"));
    }

}
