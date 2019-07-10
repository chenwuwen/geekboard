package cn.kanyun.geekboard;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * 在 Application 模块中，可创建一个添加有 @GlideModule 注解，继承自 AppGlideModule 的类。
 * 此类可生成出一个流式 API，内联了多种选项，和集成库中自定义的选项：
 * 生成的 API 默认名为 GlideApp ，与 AppGlideModule 的子类包名相同。
 * 在 Application 模块中将 Glide.with() 替换为 GlideApp.with()，即可使用该 API 去完成加载工作。
 *
 * https://www.jianshu.com/p/277efec734c9
 */
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
