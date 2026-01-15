package jp.ac.ok_oic.ikzm.careerlog.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

/**
 * 全リクエストに対してUTF-8エンコーディングを設定するフィルター
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

	/**
	 * 文字エンコーディングをUTF-8に固定して後続処理へ渡す。
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		chain.doFilter(request, response);
	}
}