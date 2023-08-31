use std::ffi::c_int;
use jni::sys::jint;
use jni::JNIEnv;
use jni::objects::JObject;
pub fn add(left: i32, right: i32) -> i32 {
    left + right
}


#[no_mangle]
pub extern "system" fn Java_PluginSignIn_add(_env:JNIEnv,_obj:JObject, left: jint, right: jint) -> jint {
    add(left, right)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
