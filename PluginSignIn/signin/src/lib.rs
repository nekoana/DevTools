use jni::JNIEnv;
use jni::objects::JObject;

#[no_mangle]
pub extern "system" fn Java_SignInViewModel_version<'a>(env:JNIEnv<'a>,_obj:JObject<'a>) -> JObject<'a> {
    let version = env.new_string("1.0.0").expect("Couldn't create java string!");

   JObject::from(version)
}
