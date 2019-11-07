package com.dbumama.market.service.api;

/**
 * 打印服务
 * @author wangjun
 *
 */
public interface PrintService {

	/**
	 * 获取快递单打印的相关数据
	 * @param printParamDto
	 * @return
	 * @throws OrderException
	 */
	public PrintExpResultDto getExpressPrintData (PrintParamDto printParamDto) throws OrderException;
	
	/**
	 * 获取快递单打印的相关数据(拼团)
	 * @param printParamDto
	 * @return
	 * @throws OrderException
	 */
	public PrintExpResultDto getExpressPrintData4Group (PrintParamDto printParamDto) throws OrderException;
	
	/**
	 * 获取发货单打印的相关数据
	 * @param printParamDto
	 * @return
	 * @throws OrderException
	 */
	public PrintInvResultDto getInvoicePrintData(PrintParamDto printParamDto) throws OrderException;
	
	/**
	 * 获取发货单打印的相关数据(拼团)
	 * @param printParamDto
	 * @return
	 * @throws OrderException
	 */
	public PrintInvResultDto getInvoicePrintData4Group(PrintParamDto printParamDto) throws OrderException;
	
}
