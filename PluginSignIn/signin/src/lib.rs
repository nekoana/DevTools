use jni::JNIEnv;
use jni::objects::{JObject, JString};
use jni::sys::jint;

extern "Rust"{
  fn api_version() -> String;

    /// 签到
    ///*  `number` 工号
    ///* `token` 令牌
    ///* `bmp_path` 签到图片路径
   fn api_signin(number: usize, token: String, bmp_path: String) -> Result<String,String>;
}



#[no_mangle]
pub extern "system" fn Java_com_kouqurong_plugin_signin_SignInViewModel_version<'a>(env:JNIEnv<'a>,_obj:JObject<'a>) -> JObject<'a> {
    let version = env.new_string("1.0.0").expect("Couldn't create java string!");

   JObject::from(version)
}

#[no_mangle]
pub extern "system" fn Java_com_kouqurong_plugin_signin_SignInViewModel_signin<'a>(mut env: JNIEnv<'a>, _obj: JObject, number: jint, token: JString, bmp_path: JString) -> JObject<'a>  {
    let token: String = env.get_string(&token).expect("Couldn't get java string!").into();
    let bmp_path: String = env.get_string(&bmp_path).expect("Couldn't get java string!").into();

    let result = unsafe {
        api_signin(number as usize, token, bmp_path)
    };

    let result = match result {
        Ok(s) => env.new_string(s).expect("Couldn't create java string!"),
        Err(e) => env.new_string(e).expect("Couldn't create java string!"),
    };

    JObject::from(result)
}


#[cfg(test)]
mod test{
    use crate::*;

    #[test]
    fn test_api_version() {
        let version = unsafe { api_version() };
        assert_eq!(version, "V1.0.0");
    }
}
