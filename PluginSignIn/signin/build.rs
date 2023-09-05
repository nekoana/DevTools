use std::env;

fn main() {
    //寻找lib路径
     let out_dir = env::var("OUT_DIR").unwrap();
    println!("cargo:rustc-link-search={}", out_dir);
    println!("cargo:rustc-link-lib=api");
}
