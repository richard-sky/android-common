package com.richard.library.net.http.request;

import com.richard.library.context.AppContext;


/**
 * <pre>
 * Description : http错误信息翻译
 * Author : admin-richard
 * Date : 2016-01-16 19:48
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2016-01-16 19:48     admin-richard         new file.
 * </pre>
 */
public final class HttpError {

    /**
     * 显示错误信息
     *
     * @param statusCode 响应状态码
     * @return 对于状态码描述信息
     */
    public static String convertErrorMessage(int statusCode) {
        if (!AppContext.isDebug()) {
            return String.format("请稍后再试哦-000%s", statusCode);
        }

        String errorMessage;

        switch (statusCode) {
            case 400:
                errorMessage = "请求不能被解析或者缺少必须的参数";
                break;
            case 401:
                errorMessage = "验证失败或者当前用户没有权限";
                break;
            case 403:
                errorMessage = "访问被拒绝";
                break;
            case 404:
                errorMessage = "未找到资源";
                break;
            case 406:
                errorMessage = "请求内容特性无法满足条件，无法生成响应实体";
                break;
            case 405:
                errorMessage = "请求不支持";
                break;
            case 408:
                errorMessage = "请求超时";
                break;
            case 500:
                errorMessage = "服务器内部错误";
                break;
            case 501:
                errorMessage = "服务器不支持该请求";
                break;
            case 502:
                errorMessage = "网关错误";
                break;
            case 503:
                errorMessage = "服务器由于维护或者负载过重未能应答";
                break;
            case 504:
                errorMessage = "网关超时";
                break;
            default:
                errorMessage = "未知错误";
        }

        return String.format("%s-[errorCode=%s]", errorMessage, String.valueOf(statusCode));
    }


    // 400 Bad Request 请求出现语法错误。
    // 401 Unauthorized 客户试图未经授权访问受密码保护的页面。应答中会包含一个WWW-Authenticate头，浏览器据此显示用户名字/密码对话框，然后在填写合适的Authorization头后再次发出请求。
    // 403 Forbidden资源不可用。
    // 404 Not Found无法找到指定位置的资源
    // 405 Method Not Allowed 请求方法（GET、POST、HEAD、Delete、PUT、TRACE等）对指定的资源不适用。
    // 408 Request Timeout  在服务器许可的等待时间内，客户一直没有发出任何请求。客户可以在以后重复同一请求。
    // 409 Conflict 通常和PUT请求有关。由于请求和资源的当前状态相冲突，因此请求不能成功。
    // 410 Gone 所请求的文档已经不再可用，而且服务器不知道应该重定向到哪一个地址。它和404的不同在于，返回407表示文档永久地离开了指定的位置，而404表示由于未知的原因文档不可用。
    // 411 Length Required 服务器不能处理请求，除非客户发送一个Content-Length头。
    // 412 Precondition Failed 请求头中指定的一些前提条件失败
    // 413 Request Entity Too Large 目标文档的大小超过服务器当前愿意处理的大小。如果服务器认为自己能够稍后再处理该请求，则应该提供一个Retry-After头
    // 414 Request URI Too Long URI太长
    // 416 Requested Range Not Satisfiable 服务器不能满足客户在请求中指定的Range头
    // 500 Internal Server Error 服务器遇到了意料不到的情况，不能完成客户的请求
    // 501 Not Implemented 服务器不支持实现请求所需要的功能。例如，客户发出了一个服务器不支持的PUT请求
    // 502 Bad Gateway 服务器作为网关或者代理时，为了完成请求访问下一个服务器，但该服务器返回了非法的应答
    // 503 Service Unavailable 服务器由于维护或者负载过重未能应答。例如，Servlet可能在数据库连接池已满的情况下返回503。服务器返回503时可以提供一个Retry-After头
    // 504 Gateway Timeout 由作为代理或网关的服务器使用，表示不能及时地从远程服务器获得应答

}
