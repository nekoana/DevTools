fn main() {
    //寻找lib路径
    let lib_dir = std::env::current_dir().unwrap().join("lib");
    println!("cargo:rustc-link-lib=api");
    println!("cargo:rustc-link-search={}", lib_dir.display());
}
