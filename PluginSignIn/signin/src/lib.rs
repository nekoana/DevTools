use jni::JNIEnv;
use jni::objects::JObject;

extern "Rust"{
  fn api_version() -> String;

}



#[no_mangle]
pub extern "system" fn Java_SignInViewModel_version<'a>(env:JNIEnv<'a>,_obj:JObject<'a>) -> JObject<'a> {
    let version = env.new_string("1.0.0").expect("Couldn't create java string!");

   JObject::from(version)
}


#[cfg(test)]
mod test{
    use crate::api_version;

    #[test]
    fn test_api_version() {
        let version = unsafe { api_version() };
        assert_eq!(version, "V1.0.0");
    }

}
