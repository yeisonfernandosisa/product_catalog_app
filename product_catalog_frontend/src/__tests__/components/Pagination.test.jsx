import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import Pagination from '../../components/Pagination'

describe('Pagination', () => {
  it('moves to the next page when the next button is clicked', () => {
    const onChange = vi.fn()
    render(<Pagination page={0} totalPages={3} onChange={onChange} />)

    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }))

    expect(onChange).toHaveBeenCalledWith(1)
  })

  it('disables the previous button on the first page', () => {
    render(<Pagination page={0} totalPages={3} onChange={vi.fn()} />)

    expect(screen.getByRole('button', { name: 'Anterior' })).toBeDisabled()
  })
})
